package sg.edu.nus.comp.cs3219.viz.ui.controller.api;

import org.junit.Test;
import org.springframework.http.MediaType;

import static org.hamcrest.Matchers.hasSize;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.delete;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.get;
import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.post;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.jsonPath;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.status;

import sg.edu.nus.comp.cs3219.viz.BaseTestREST;

public class PresentationControllerTest extends BaseTestREST {

    @Override
    protected String getDataBundleName() {
        return "/PresentationRepositoryTest.json";
    }

    @Test
    public void getAllPresentations_loggedOut_failed() throws Exception {
        gaeSimulation.logoutUser();

        mvc.perform(get("/api/presentations"))
                .andExpect(status().isUnauthorized());
    }




    @Test
    public void getAllPresentations_loggedIn_success() throws Exception {
        gaeSimulation.loginUser("test1@viz.test");

        mvc.perform(get("/api/presentations"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$").isArray())
                .andExpect(jsonPath("$", hasSize(2)));
    }

    @Test
    public void getSinglePresentations_failure_notFound() throws Exception {
        gaeSimulation.loginUser("test1@viz.test");

        mvc.perform(get("/api/presentations/100"))
                .andExpect(status().isNotFound());

    }

    @Test
    public void getSinglePresentations_success() throws Exception {
        gaeSimulation.loginUser("test1@viz.test");

        mvc.perform(get("/api/presentations/1"))
                .andExpect(status().isOk())
                .andExpect(jsonPath("$.name").value("testA1"))
                .andExpect(jsonPath("$.creatorIdentifier").value("test1@viz.test"));
    }

    @Test
    public void addPresentation_success() throws Exception {

        gaeSimulation.loginUser("test1@viz.test");

        mvc.perform(post("/api/presentations")
                .contentType(MediaType.APPLICATION_JSON)
                .content(objectToJson(dataBundle.presentations.get("presentationC"))))
                .andExpect(status().isCreated());

    }

    @Test
    public void deletePresentation_success() throws Exception {
        gaeSimulation.loginUser("test2@viz.test");

        mvc.perform(delete("/api/presentations/3"))
                .andExpect(status().isNoContent());

    }



}
