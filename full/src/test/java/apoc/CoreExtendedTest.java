package apoc;

import apoc.util.Neo4jContainerExtension;
import apoc.util.TestUtil;
import org.junit.Test;
import org.neo4j.driver.Session;

import static apoc.util.TestContainerUtil.createEnterpriseDB;
import static org.junit.Assert.assertTrue;
import static org.junit.Assert.fail;

/*
 This test is just to verify if the APOC are correctly deployed
 into a Neo4j instance without any startup issue.
 If you don't have docker installed it will fail, and you can simply ignore it.
 */
public class CoreExtendedTest {
    @Test
    public void test() {
        try {
            Neo4jContainerExtension neo4jContainer = createEnterpriseDB(!TestUtil.isTravis())
                    .withNeo4jConfig("dbms.transaction.timeout", "5s");

            neo4jContainer.start();

            assertTrue("Neo4j Instance should be up-and-running", neo4jContainer.isRunning());

            Session session = neo4jContainer.getSession();
            int coreCount = session.run("CALL apoc.help('') YIELD core WHERE core = true RETURN count(*) AS count").peek().get("count").asInt();
            int extendedCount = session.run("CALL apoc.help('') YIELD core WHERE core = false RETURN count(*) AS count").peek().get("count").asInt();

            assertTrue(coreCount > 0);
            assertTrue(extendedCount > 0);

            neo4jContainer.close();
        } catch (Exception ex) {
            ex.printStackTrace();
            fail("Should not have thrown exception when trying to start Neo4j: " + ex);
        }
    }
}
