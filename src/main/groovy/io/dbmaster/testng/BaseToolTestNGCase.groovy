package io.dbmaster.testng;


import com.google.inject.Inject;

import com.branegy.tools.api.ToolService;

public abstract class BaseToolTestNGCase extends BaseServiceTestNGCase {
    @Inject
    protected ToolService tools;
}
