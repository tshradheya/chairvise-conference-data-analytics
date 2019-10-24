package sg.edu.nus.comp.cs3219.viz.ui.controller.api;

import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import sg.edu.nus.comp.cs3219.viz.common.datatransfer.UserInfo;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.AuthorRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.ReviewRecord;
import sg.edu.nus.comp.cs3219.viz.common.entity.record.SubmissionRecord;
import sg.edu.nus.comp.cs3219.viz.logic.GateKeeper;
import sg.edu.nus.comp.cs3219.viz.logic.RecordLogic;

import java.net.URI;
import java.net.URISyntaxException;
import java.util.List;

@RestController
public class RecordController extends BaseRestController {

    private GateKeeper gateKeeper;

    private RecordLogic recordLogic;

    public RecordController(GateKeeper gateKeeper, RecordLogic recordLogic) {
        this.gateKeeper = gateKeeper;
        this.recordLogic = recordLogic;
    }

    @PostMapping("/record/author/{confName}")
    public ResponseEntity<?> importAuthorRecord(@RequestBody List<AuthorRecord> authorRecordList, @PathVariable String confName) throws URISyntaxException {
        UserInfo userInfo = gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistAuthorRecordForDataSet(userInfo.getUserEmail(), authorRecordList, confName);

        return ResponseEntity.created(new URI("/record/author")).build();
    }

    @PostMapping("/record/review/{confName}")
    public ResponseEntity<?> importReviewRecord(@RequestBody List<ReviewRecord> reviewRecordList, @PathVariable String confName) throws URISyntaxException {
        UserInfo userInfo = gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistReviewRecordForDataSet(userInfo.getUserEmail(), reviewRecordList, confName);

        return ResponseEntity.created(new URI("/record/review")).build();
    }

    @PostMapping("/record/submission/{confName}")
    public ResponseEntity<?> importSubmissionRecord(@RequestBody List<SubmissionRecord> submissionRecords, @PathVariable String confName) throws URISyntaxException {
        UserInfo userInfo = gateKeeper.verifyLoginAccess();

        this.recordLogic.removeAndPersistSubmissionRecordForDataSet(userInfo.getUserEmail(), submissionRecords, confName);

        return ResponseEntity.created(new URI("/record/review")).build();
    }

    @GetMapping("/allConferenceNames")
    public List<String> getAllConferenceNames() {
        UserInfo userInfo = gateKeeper.verifyLoginAccess();

        return this.recordLogic.getAllConferenceNames(userInfo.getUserEmail());
    }
}
