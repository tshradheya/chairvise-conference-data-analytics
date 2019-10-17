package sg.edu.nus.comp.cs3219.viz.ui.controller.api;

import org.junit.Assert;
import org.junit.Test;
import org.springframework.http.MediaType;
import sg.edu.nus.comp.cs3219.viz.BaseTestREST;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

public class RecordControllerTest extends BaseTestREST {

    private String CONF_NAME = "defaultConf";

    @Override
    protected String getDataBundleName() {
        return "/RecordControllerTest.json";
    }

    @Test
    public void testImportAuthorRecord_notLogin_shouldNotAccess() throws Exception {
        gaeSimulation.logoutUser();

        mvc.perform(
                post("/api/record/author/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.authorRecords.values())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testImportReviewRecord_notLogin_shouldNotAccess() throws Exception {
        gaeSimulation.logoutUser();

        mvc.perform(
                post("/api/record/review/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.reviewRecords.values())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testImportSubmissionRecord_notLogin_shouldNotAccess() throws Exception {
        gaeSimulation.logoutUser();

        mvc.perform(
                post("/api/record/submission/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.submissionRecords.values())))
                .andExpect(status().isUnauthorized());
    }

    @Test
    public void testImportAuthorRecord_login_shouldOverrideExistingRecord() throws Exception {
        gaeSimulation.loginUser("test@example.com");

        mvc.perform(
                post("/api/record/author/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.authorRecords.values())))
                .andExpect(status().isCreated());

        Assert.assertEquals(1,
                authorRecordRepository.findByDataSetEquals("test@example.com").size());
        Assert.assertFalse(
                authorRecordRepository.findById(dataBundle.authorRecords.get("authorS1").getId()).isPresent());
    }

    @Test
    public void testImportReviewRecord_login_shouldOverrideExistingRecord() throws Exception {
        gaeSimulation.loginUser("test@example.com");

        mvc.perform(
                post("/api/record/review/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.reviewRecords.values())))
                .andExpect(status().isCreated());

        Assert.assertEquals(1,
                reviewRecordRepository.findByDataSetEquals("test@example.com").size());
        Assert.assertFalse(
                reviewRecordRepository.findById(dataBundle.reviewRecords.get("review1S1").getId()).isPresent());
    }

    @Test
    public void testImportSubmissionRecord_login_shouldOverrideExistingRecord() throws Exception {
        gaeSimulation.loginUser("test@example.com");

        mvc.perform(
                post("/api/record/submission/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.submissionRecords.values())))
                .andExpect(status().isCreated());

        Assert.assertEquals(1,
                submissionRecordRepository.findByDataSetEquals("test@example.com").size());
        Assert.assertFalse(
                submissionRecordRepository.findById(dataBundle.submissionRecords.get("s1").getId()).isPresent());
    }

    @Test
    public void testImportAuthorRecord_loginDifferentUser_shouldWriteDifferentDataSetRecord() throws Exception {
        gaeSimulation.loginUser("test1@example.com");

        // pass dataSet maliciously
        Assert.assertNotEquals("test1@example.com", dataBundle.authorRecords.get("authorS1").getDataSet());

        mvc.perform(
                post("/api/record/author/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.authorRecords.values())))
                .andExpect(status().isCreated());

        Assert.assertEquals(1,
                authorRecordRepository.findByDataSetEquals("test@example.com").size());
        Assert.assertEquals(1,
                authorRecordRepository.findByDataSetEquals("test1@example.com").size());
    }

    @Test
    public void testImportReviewRecord_loginDifferentUser_shouldWriteDifferentDataSetRecord() throws Exception {
        gaeSimulation.loginUser("test1@example.com");

        // pass dataSet maliciously
        Assert.assertNotEquals("test1@example.com", dataBundle.reviewRecords.get("review1S1").getDataSet());

        mvc.perform(
                post("/api/record/review/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.reviewRecords.values())))
                .andExpect(status().isCreated());

        Assert.assertEquals(1,
                reviewRecordRepository.findByDataSetEquals("test@example.com").size());
        Assert.assertEquals(1,
                reviewRecordRepository.findByDataSetEquals("test1@example.com").size());
    }

    @Test
    public void testImportSubmissionRecord_loginDifferentUser_shouldWriteDifferentDataSetRecord() throws Exception {
        gaeSimulation.loginUser("test1@example.com");

        // pass dataSet maliciously
        Assert.assertNotEquals("test1@example.com", dataBundle.submissionRecords.get("s1").getDataSet());

        mvc.perform(
                post("/api/record/submission/" + CONF_NAME)
                        .contentType(MediaType.APPLICATION_JSON)
                        .content(objectToJson(dataBundle.submissionRecords.values())))
                .andExpect(status().isCreated());

        Assert.assertEquals(1,
                submissionRecordRepository.findByDataSetEquals("test@example.com").size());
        Assert.assertEquals(1,
                submissionRecordRepository.findByDataSetEquals("test1@example.com").size());
    }

    @Test
    public void testGetConferenceNames_unauthorizedIfNotLoggedIn() throws Exception {
        mvc.perform((get("/api/allConferenceNames")))
                .andExpect(status().isUnauthorized());

    }

    @Test
    public void testGetConferenceNames_shouldReturnCorrectData() throws Exception {
        gaeSimulation.loginUser("test@example.com");

        mvc.perform((get("/api/allConferenceNames")))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(1)))
                .andExpect(jsonPath("$[0]").value(CONF_NAME))
        ;
    }
}
