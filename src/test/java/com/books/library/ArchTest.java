package com.books.library;

import com.tngtech.archunit.core.domain.JavaClasses;
import com.tngtech.archunit.core.importer.ClassFileImporter;
import com.tngtech.archunit.core.importer.ImportOption;
import org.junit.jupiter.api.Test;

import static com.tngtech.archunit.lang.syntax.ArchRuleDefinition.noClasses;

class ArchTest {

    @Test
    void servicesAndRepositoriesShouldNotDependOnWebLayer() {

        JavaClasses importedClasses = new ClassFileImporter()
            .withImportOption(ImportOption.Predefined.DO_NOT_INCLUDE_TESTS)
            .importPackages("com.books.library");

        noClasses()
            .that()
                .resideInAnyPackage("com.books.library.service..")
            .or()
                .resideInAnyPackage("com.books.library.repository..")
            .should().dependOnClassesThat()
                .resideInAnyPackage("..com.books.library.web..")
        .because("Services and repositories should not depend on web layer")
        .check(importedClasses);
    }
}
