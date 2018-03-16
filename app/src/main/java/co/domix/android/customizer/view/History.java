package co.domix.android.customizer.view;

import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.support.v7.widget.Toolbar;

import co.domix.android.DomixApplication;
import co.domix.android.R;
import co.domix.android.customizer.presenter.HistoryPresenter;
import co.domix.android.customizer.presenter.HistoryPresenterImpl;

public class History extends AppCompatActivity implements HistoryView {

    private RecyclerView rv;
    private HistoryPresenter presenter;
    private DomixApplication app;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_history);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);
        getSupportActionBar().setDisplayHomeAsUpEnabled(true);
        getSupportActionBar().setTitle(R.string.title_activity_user_history);

        app = (DomixApplication) getApplicationContext();
        presenter = new HistoryPresenterImpl(this);

        rv = (RecyclerView) findViewById(R.id.recyclerOrderHistory);
        rv.setLayoutManager(new LinearLayoutManager(this));

        listOrder(rv);
    }

    @Override
    public void listOrder(RecyclerView rv) {
        presenter.listOrder(this, rv, app.uId);
    }
}
