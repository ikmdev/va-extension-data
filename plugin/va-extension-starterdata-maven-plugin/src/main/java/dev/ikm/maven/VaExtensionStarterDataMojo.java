package dev.ikm.maven;

import dev.ikm.tinkar.common.service.CachingService;
import dev.ikm.tinkar.common.service.PrimitiveData;
import dev.ikm.tinkar.common.service.ServiceKeys;
import dev.ikm.tinkar.common.service.ServiceProperties;
import dev.ikm.tinkar.common.util.uuid.UuidT5Generator;
import dev.ikm.tinkar.composer.Composer;
import dev.ikm.tinkar.composer.Session;
import dev.ikm.tinkar.composer.assembler.ConceptAssembler;
import dev.ikm.tinkar.composer.template.Definition;
import dev.ikm.tinkar.composer.template.FullyQualifiedName;
import dev.ikm.tinkar.composer.template.Identifier;
import dev.ikm.tinkar.composer.template.StatedAxiom;
import dev.ikm.tinkar.composer.template.Synonym;
import dev.ikm.tinkar.terms.EntityProxy;
import dev.ikm.tinkar.terms.State;
import dev.ikm.tinkar.terms.TinkarTerm;
import org.apache.maven.plugin.AbstractMojo;
import org.apache.maven.plugin.MojoExecutionException;
import org.apache.maven.plugins.annotations.LifecyclePhase;
import org.apache.maven.plugins.annotations.Mojo;
import org.apache.maven.plugins.annotations.Parameter;

import java.io.File;
import java.util.UUID;

import static dev.ikm.tinkar.terms.TinkarTerm.DESCRIPTION_NOT_CASE_SENSITIVE;
import static dev.ikm.tinkar.terms.TinkarTerm.ENGLISH_LANGUAGE;

@Mojo(name = "run-va-extension-starterdata", defaultPhase = LifecyclePhase.INSTALL)
public class VaExtensionStarterDataMojo extends AbstractMojo
{
    @Parameter(property = "origin.namespace", required = true)
    String namespaceString;
    @Parameter(property = "datastorePath", required = true)
    private String datastorePath;
    @Parameter(property = "controllerName", defaultValue = "Open SpinedArrayStore")
    private String controllerName;

    public void execute() throws MojoExecutionException
    {
        try {
            UUID namespace = UUID.fromString(namespaceString);
            File datastore = new File(datastorePath);

            CachingService.clearAll();
            ServiceProperties.set(ServiceKeys.DATA_STORE_ROOT, datastore);
            PrimitiveData.selectControllerByName(controllerName);
            PrimitiveData.start();

            Composer composer = new Composer("VA Extension Starter Data Composer");
            EntityProxy.Concept vaExtensionAuthor = EntityProxy.Concept.make("VA Extension Starter Data Author", UuidT5Generator.get(namespace, "VA Extension Starter Data Author"));

            Session session = composer.open(State.ACTIVE,
                    vaExtensionAuthor,
                    TinkarTerm.PRIMORDIAL_MODULE,
                    TinkarTerm.PRIMORDIAL_PATH);

            session.compose((ConceptAssembler concept) -> concept
                    .concept(vaExtensionAuthor)
                    .attach((FullyQualifiedName fqn) -> fqn
                            .language(ENGLISH_LANGUAGE)
                            .text("VA Extension Starter Data Author")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Synonym synonym)-> synonym
                            .language(ENGLISH_LANGUAGE)
                            .text("VA Extension Starter Data Author")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Definition definition) -> definition
                            .language(ENGLISH_LANGUAGE)
                            .text("VA Snomed Extension Starter Data Author")
                            .caseSignificance(DESCRIPTION_NOT_CASE_SENSITIVE)
                    )
                    .attach((Identifier identifier) -> identifier
                            .source(TinkarTerm.UNIVERSALLY_UNIQUE_IDENTIFIER)
                            .identifier(vaExtensionAuthor.asUuidArray()[0].toString())
                    )
                    .attach((StatedAxiom statedAxiom) -> statedAxiom
                            .isA(TinkarTerm.USER)
                    )
            );

            composer.commitSession(session);
            PrimitiveData.stop();
        } catch (Exception e) {
            throw new MojoExecutionException("Failed to execute class", e);
        }

    }
}
