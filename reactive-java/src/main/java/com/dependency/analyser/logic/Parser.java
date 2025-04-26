package com.dependency.analyser.logic;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.HashSet;
import java.util.Optional;
import java.util.Set;

import com.github.javaparser.JavaParser;
import com.github.javaparser.ParseResult;
import com.github.javaparser.ast.CompilationUnit;
import com.github.javaparser.ast.ImportDeclaration;
import com.github.javaparser.ast.body.ClassOrInterfaceDeclaration;

import io.reactivex.rxjava3.core.Flowable;

public class Parser {
    JavaParser parser;

    public Parser() {
        this.parser = new JavaParser();
    }

    public class DependencyInfo {
        public final String className;
        public final String packageName;
        public final Set<String> dependencies;

        public DependencyInfo(String className, String packageName, Set<String> dependencies) {
            this.className = className;
            this.packageName = packageName;
            this.dependencies = dependencies;
        }
    }

    public Flowable<DependencyInfo> analyse(Path rootFolder) {
        return Flowable.fromIterable(() -> {
            try {
                return Files.walk(rootFolder).iterator();
            } catch (IOException e) {
                throw new RuntimeException(e);
            }
        })
                .filter(p -> p.toString().endsWith(".java"))
                .map(file -> {
                    // Simulate a delay for the sake of the example
                    // try {
                    //     Thread.sleep(300);
                    // } catch (InterruptedException e) {
                    //     e.printStackTrace();
                    // }

                    ParseResult<CompilationUnit> result = this.parser.parse(file);
                    Set<String> imports = new HashSet<>();
                    String className = "";
                    String packageName = "";

                    if (result.isSuccessful() && result.getResult().isPresent()) {
                        CompilationUnit cu = result.getResult().get();

                        Optional<ClassOrInterfaceDeclaration> classDecl = cu
                                .findFirst(ClassOrInterfaceDeclaration.class);
                        className = classDecl.map(c -> c.getNameAsString()).orElse("Anonima");

                        packageName = cu.getPackageDeclaration()
                                .map(p -> p.getNameAsString())
                                .orElse("");

                        cu.findAll(ImportDeclaration.class).forEach(imp -> imports.add(imp.getNameAsString()));
                    }

                    return new DependencyInfo(className, packageName, imports);
                });
    }
}
