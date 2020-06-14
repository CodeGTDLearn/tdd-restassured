package com.example.demo;

import com.example.demo.repo.RepoTest;
import com.example.demo.resource.*;
import com.example.demo.servico.ExcecoesTest;
import com.example.demo.servico.ServiceTest;
import org.junit.After;
import org.junit.Before;
import org.junit.runner.RunWith;
import org.junit.runners.Suite;

@RunWith(Suite.class)
@Suite.SuiteClasses({
        RepoTest.class ,
        ServiceTest.class ,
        ExcecoesTest.class ,
        RA_MockmvcUnit.class ,
        RA_MockmvcIntegr.class ,
        Spring_Mockmvc.class ,
        RA_CRUD.class ,
        RA_Param.class ,
        RA_Schema.class ,
        RA_Authentic_jwt.class ,
        RA_HTML_HtmlForms.class ,
        RA_Request_Response.class ,
        RA_JsonCursoRestAssure.class ,
        RA_DownUpload.class
})
public class SuiteDeTestes extends GlobalTestConfig {
    @Before
    public void setUp() {
        System.out.println("----- SUPER SETUP -----");
    }

    @After
    public void tearDown() {
        System.out.println("----- SUPER TEARDOWN -----");
    }
}
