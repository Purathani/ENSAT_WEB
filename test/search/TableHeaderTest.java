/*
 * To change this template, choose Tools | Templates
 * and open the template in the editor.
 */
package search;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertArrayEquals;
import org.junit.*;

/**
 *
 * @author chris
 */
public class TableHeaderTest {
    
    String[] columnTest = {"table_id","patient_id","center_id","biomaterial_date","tumor_tissue_frozen",
                            "tumor_tissue_ensat_sop","tumor_tissue_paraffin","tumor_tissue_dna"};
    String[] subTest = {"subtable_id","table_id","patient_id","center_id","biomaterial_extra_A","biomaterial_extra_B"};
    
    public TableHeaderTest() {
    }

    @BeforeClass
    public static void setUpClass() throws Exception {
    }

    @AfterClass
    public static void tearDownClass() throws Exception {
    }
    
    @Before
    public void setUp() {
    }
    
    @After
    public void tearDown() {
    }
    // TODO add test methods here.
    // The methods must be annotated with annotation @Test. For example:
    //
    @Test
    public void createSingleEntryNoSubtable() {
        TableHeader testMain = new TableHeader(columnTest, 1);
        String[] results = {"patient_id","table_id","biomaterial_date","tumor_tissue_frozen",
                            "tumor_tissue_ensat_sop","tumor_tissue_paraffin","tumor_tissue_dna"};
        assertArrayEquals(results,testMain.getHeaders());
    }
    
    @Test
    public void createMultipleEntryNoSubtable() {
        TableHeader testMain = new TableHeader(columnTest, 2);
        String[] results = {"patient_id","table_id_1","biomaterial_date_1","tumor_tissue_frozen_1",
                            "tumor_tissue_ensat_sop_1","tumor_tissue_paraffin_1","tumor_tissue_dna_1",
                            "table_id_2","biomaterial_date_2","tumor_tissue_frozen_2",
                            "tumor_tissue_ensat_sop_2","tumor_tissue_paraffin_2","tumor_tissue_dna_2"
        };
        
        System.err.println(Arrays.toString(testMain.getHeaders()));
        assertArrayEquals(results,testMain.getHeaders());
    }
    
    @Test
    public void createSingleEntryPlusSubtable() {
        TableHeader testMain = new TableHeader(columnTest, 1);
        testMain.addSubTable(subTest, 1);
        String[] results = {"patient_id","table_id_1","biomaterial_date_1","tumor_tissue_frozen_1",
                            "tumor_tissue_ensat_sop_1","tumor_tissue_paraffin_1","tumor_tissue_dna_1","biomaterial_extra_A_1_1","biomaterial_extra_B_1_1"};
        System.err.println(Arrays.toString(testMain.getHeaders()));
        assertArrayEquals(results,testMain.getHeaders());
    }

    @Test
    public void createSingleEntryPlusSubtables() {
        TableHeader testMain = new TableHeader(columnTest, 1);
        testMain.addSubTable(subTest, 2);
        String[] results = {"patient_id","table_id_1","biomaterial_date_1","tumor_tissue_frozen_1",
                            "tumor_tissue_ensat_sop_1","tumor_tissue_paraffin_1","tumor_tissue_dna_1",
                            "biomaterial_extra_A_1_1","biomaterial_extra_B_1_1",
                            "biomaterial_extra_A_1_2","biomaterial_extra_B_1_2"};
        System.err.println(Arrays.toString(testMain.getHeaders()));
        assertArrayEquals(results,testMain.getHeaders());
    }
    
    @Test
    public void createMultipleEntryPlusSubtable() {
        TableHeader testMain = new TableHeader(columnTest, 2);
        testMain.addSubTable(subTest, 1);
        String[] results = {"patient_id","table_id_1","biomaterial_date_1","tumor_tissue_frozen_1",
                            "tumor_tissue_ensat_sop_1","tumor_tissue_paraffin_1","tumor_tissue_dna_1",
                            "biomaterial_extra_A_1_1","biomaterial_extra_B_1_1",
                            "table_id_2","biomaterial_date_2","tumor_tissue_frozen_2",
                            "tumor_tissue_ensat_sop_2","tumor_tissue_paraffin_2","tumor_tissue_dna_2",
                            "biomaterial_extra_A_2_1","biomaterial_extra_B_2_1",};
        assertArrayEquals(results,testMain.getHeaders());
    }
    
    @Test
    public void createMultipleEntryPlusSubtables() {
        TableHeader testMain = new TableHeader(columnTest, 2);
        testMain.addSubTable(subTest, 2);
        String[] results = {"patient_id","table_id_1","biomaterial_date_1","tumor_tissue_frozen_1",
                            "tumor_tissue_ensat_sop_1","tumor_tissue_paraffin_1","tumor_tissue_dna_1",
                            "biomaterial_extra_A_1_1","biomaterial_extra_B_1_1",
                            "biomaterial_extra_A_1_2","biomaterial_extra_B_1_2",
                            "table_id_2","biomaterial_date_2","tumor_tissue_frozen_2",
                            "tumor_tissue_ensat_sop_2","tumor_tissue_paraffin_2","tumor_tissue_dna_2",
                            "biomaterial_extra_A_2_1","biomaterial_extra_B_2_1",
                            "biomaterial_extra_A_2_2","biomaterial_extra_B_2_2"};
        assertArrayEquals(results,testMain.getHeaders());
    }
    
    @Test
    public void createEmptyResult() {
        TableHeader testMain = new TableHeader(columnTest,0);
        String[] results = {};
        assertArrayEquals(results,testMain.getHeaders());
    }
}
