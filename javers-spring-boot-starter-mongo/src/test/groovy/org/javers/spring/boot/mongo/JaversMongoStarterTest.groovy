package org.javers.spring.boot.mongo

import org.javers.core.CommitIdGenerator
import org.javers.core.Javers
import org.javers.core.MappingStyle
import org.javers.core.diff.ListCompareAlgorithm
import org.javers.core.metamodel.type.EntityType
import org.javers.spring.auditable.AuthorProvider
import org.javers.spring.auditable.SpringSecurityAuthorProvider
import org.springframework.beans.factory.annotation.Autowired
import org.springframework.boot.test.context.SpringBootTest
import org.springframework.test.context.ActiveProfiles
import spock.lang.Specification

/**
 * @author pawelszymczyk
 */
@SpringBootTest(classes = [TestApplication])
@ActiveProfiles("test")
class JaversMongoStarterTest extends Specification{

    @Autowired
    Javers javers

    @Autowired
    JaversMongoProperties javersProperties

    @Autowired
    AuthorProvider provider

    def "shoudUseDbNameFromMongoStarter"(){
        expect:
        javers.repository.delegate.mongoSchemaManager.mongo.name == "spring-mongo"
    }

    def "shouldReadConfigurationFromYml"() {
        expect:
        javers.configuration.listCompareAlgorithm == ListCompareAlgorithm.LEVENSHTEIN_DISTANCE
        javers.configuration.mappingStyle == MappingStyle.BEAN
       !javers.configuration.newObjectChanges
       !javers.configuration.removedObjectChanges
        javers.configuration.commitIdGenerator == CommitIdGenerator.RANDOM

        javersProperties.algorithm == "levenshtein_distance"
        javersProperties.mappingStyle == "bean"
       !javersProperties.newObjectChanges
       !javersProperties.removedObjectChanges
        javersProperties.commitIdGenerator=="random"
        javersProperties.prettyPrint
        javersProperties.typeSafeValues
        javersProperties.documentDbCompatibilityEnabled == true
        javersProperties.objectAccessHook == "org.javers.spring.boot.mongo.DummyDBRefUnproxyObjectAccessHook"
        javersProperties.snapshotsCacheSize == 100
    }

    def "shouldReadBeanMappingStyleFromYml"() {
        expect:
        javers.getTypeMapping(DummyEntity) instanceof EntityType
    }

    def "shouldHaveSpringSecurityAuthorProviderWhenSpringSecurityOnClasspath"() {
        expect:
        provider instanceof SpringSecurityAuthorProvider
    }
}
