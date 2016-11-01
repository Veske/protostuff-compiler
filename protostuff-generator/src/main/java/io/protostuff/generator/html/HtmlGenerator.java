package io.protostuff.generator.html;

import com.google.common.base.Throwables;
import io.protostuff.compiler.model.Module;
import io.protostuff.generator.CompilerUtils;
import io.protostuff.generator.ProtoCompiler;
import io.protostuff.generator.html.json.enumeration.JsonEnumGenerator;
import io.protostuff.generator.html.json.index.JsonIndexGenerator;
import io.protostuff.generator.html.json.message.JsonMessageGenerator;
import io.protostuff.generator.html.json.pages.JsonPagesGenerator;
import io.protostuff.generator.html.json.proto.JsonProtoGenerator;
import io.protostuff.generator.html.json.service.JsonServiceGenerator;
import org.apache.commons.io.FileUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import java.io.File;
import java.util.List;
import javax.inject.Inject;

/**
 * @author Kostiantyn Shchepanovskyi
 */
public class HtmlGenerator implements ProtoCompiler {

    public static final String HTML_RESOURCE_BASE = "io/protostuff/generator/html/";
    public static final String[] STATIC_RESOURCES = new String[]{
            "index.html",
            "partials/type-list.html",
            "partials/type-detail.html",
            "partials/proto-detail.html",
            "partials/enum.html",
            "partials/message.html",
            "partials/service.html",
            "partials/type-ref.html",
            "partials/scalar-value-types.html",
            "js/app.js",
            "js/controllers.js",
            "js/filters.js",
            "js/directives.js",
            "css/theme.css",
    };

    public static final String WEBJARS_RESOURCE_PREFIX = "META-INF/resources/webjars/";

    public static final String[] STATIC_LIBS = new String[]{
            "jquery/1.11.1/jquery.min.js",
            "bootstrap/3.3.5/js/bootstrap.min.js",
            "bootstrap/3.3.5/css/bootstrap.min.css",
            "bootstrap/3.3.5/css/bootstrap-theme.min.css",
            "bootstrap/3.3.5/fonts/glyphicons-halflings-regular.woff2",
            "bootstrap/3.3.5/fonts/glyphicons-halflings-regular.svg",
            "bootstrap/3.3.5/fonts/glyphicons-halflings-regular.woff",
            "bootstrap/3.3.5/fonts/glyphicons-halflings-regular.eot",
            "bootstrap/3.3.5/fonts/glyphicons-halflings-regular.ttf",
            "angular-bootstrap-nav-tree-gildo/0.1.0/dist/abn_tree_directive.js",
            "angular-bootstrap-nav-tree-gildo/0.1.0/dist/abn_tree.css",
            "angularjs/1.4.7/angular.min.js",
            "angularjs/1.4.7/angular-animate.min.js",
            "angularjs/1.4.7/angular-route.min.js",
    };

    private static final Logger LOGGER = LoggerFactory.getLogger(HtmlGenerator.class);
    public static final String PAGES = "pages";
    private final CompilerUtils compilerUtils;
    private final JsonIndexGenerator indexGenerator;
    private final JsonEnumGenerator enumGenerator;
    private final JsonMessageGenerator messageGenerator;
    private final JsonServiceGenerator serviceGenerator;
    private final JsonProtoGenerator protoGenerator;
    private final JsonPagesGenerator pagesGenerator;

    @Inject
    public HtmlGenerator(CompilerUtils compilerUtils,
            JsonIndexGenerator indexGenerator,
            JsonEnumGenerator enumGenerator,
            JsonMessageGenerator messageGenerator,
            JsonServiceGenerator serviceGenerator,
            JsonProtoGenerator protoGenerator,
            JsonPagesGenerator pagesGenerator) {
        this.compilerUtils = compilerUtils;
        this.indexGenerator = indexGenerator;
        this.enumGenerator = enumGenerator;
        this.messageGenerator = messageGenerator;
        this.serviceGenerator = serviceGenerator;
        this.protoGenerator = protoGenerator;
        this.pagesGenerator = pagesGenerator;
    }

    @Override
    public void compile(Module module) {
        indexGenerator.compile(module);
        enumGenerator.compile(module);
        messageGenerator.compile(module);
        serviceGenerator.compile(module);
        protoGenerator.compile(module);
        pagesGenerator.compile(module);
        copy(HTML_RESOURCE_BASE, module.getOutput() + "/", STATIC_RESOURCES);
        copy(WEBJARS_RESOURCE_PREFIX, module.getOutput() + "/libs/", STATIC_LIBS);
        copyStaticPages(module);
    }

    private void copyStaticPages(Module module) {
        try {
            @SuppressWarnings("unchecked")
            List<StaticPage> pages = (List<StaticPage>) module.getOptions().get(PAGES);
            if (pages != null) {
                File pagesDir = new File(module.getOutput() + "/pages/");
                FileUtils.forceMkdir(pagesDir);
                for (StaticPage page : pages) {
                    FileUtils.copyFileToDirectory(page.getFile(), pagesDir);
                }
            }
        } catch (Exception e) {
            throw Throwables.propagate(e);
        }
    }

    private void copy(String source, String target, String[] files) {
        for (String file : files) {
            String src = source + file;
            String dst = target + file;
            LOGGER.info("Copy {} -> {}", src, dst);
            compilerUtils.copyResource(src, dst);
        }
    }
}
