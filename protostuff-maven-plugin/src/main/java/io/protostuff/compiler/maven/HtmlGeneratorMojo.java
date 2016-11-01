package io.protostuff.compiler.maven;

import io.protostuff.compiler.model.ImmutableModuleConfiguration;
import io.protostuff.generator.CompilerModule;
import io.protostuff.generator.html.HtmlGenerator;
import io.protostuff.generator.html.StaticPage;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugin.MojoFailureException;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.io.IOException;
import java.nio.file.FileSystems;
import java.nio.file.FileVisitResult;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.PathMatcher;
import java.nio.file.SimpleFileVisitor;
import java.nio.file.attribute.BasicFileAttributes;
import java.util.List;

import io.protostuff.compiler.model.ModuleConfiguration;
import io.protostuff.generator.ProtostuffCompiler;

import static java.util.Collections.singletonList;
import static org.apache.maven.plugins.annotations.ResolutionScope.COMPILE_PLUS_RUNTIME;

/**
 * @author Kostiantyn Shchepanovskyi
 */
@Mojo(name = "html",
        configurator = "include-project-dependencies",
        requiresDependencyResolution = COMPILE_PLUS_RUNTIME)
public class HtmlGeneratorMojo extends AbstractGeneratorMojo {

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlGeneratorMojo.class);

    @Parameter(defaultValue = "${project.build.directory}/generated-html")
    private File target;

    @Parameter
    private List<StaticPage> pages;

    @Override
    public void execute() throws MojoExecutionException, MojoFailureException {
        super.execute();

        ProtostuffCompiler compiler = new ProtostuffCompiler();
        final Path sourcePath = getSourcePath();
        ImmutableModuleConfiguration.Builder builder = ImmutableModuleConfiguration.builder()
                .name("html")
                .includePaths(singletonList(sourcePath))
                .generator(CompilerModule.HTML_COMPILER)
                .output(target.getAbsolutePath());
        if (pages != null) {
            builder.putOptions(HtmlGenerator.PAGES, pages);
        }
        PathMatcher protoMatcher = FileSystems.getDefault().getPathMatcher("glob:**/*.proto");
        try {
            Files.walkFileTree(sourcePath, new SimpleFileVisitor<Path>() {
                @Override
                public FileVisitResult visitFile(Path file, BasicFileAttributes attrs) throws IOException {
                    if (protoMatcher.matches(file)) {
                        String protoFile = sourcePath.relativize(file).toString();
                        builder.addProtoFiles(normalizeProtoPath(protoFile));
                    }
                    return super.visitFile(file, attrs);
                }
            });
        } catch (IOException e) {
            LOGGER.error("Can not build source files list", e);
        }
        ModuleConfiguration moduleConfiguration = builder.build();

        LOGGER.debug("Module configuration = {}", moduleConfiguration);
        compiler.compile(moduleConfiguration);

    }

}
