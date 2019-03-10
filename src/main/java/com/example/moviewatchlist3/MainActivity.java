package com.example.moviewatchlist3;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.support.design.widget.FloatingActionButton;
import android.support.v7.widget.LinearLayoutManager;
import android.support.v7.widget.RecyclerView;
import android.util.Log;
import android.util.Xml;
import android.view.View;
import android.support.design.widget.NavigationView;
import android.support.v4.view.GravityCompat;
import android.support.v4.widget.DrawerLayout;
import android.support.v7.app.ActionBarDrawerToggle;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.Toolbar;
import android.view.Menu;
import android.view.MenuItem;

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import org.xmlpull.v1.XmlSerializer;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.StringWriter;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

public class MainActivity extends AppCompatActivity
        implements NavigationView.OnNavigationItemSelectedListener {

    private RecyclerView rvMovies;
    private MovieAdapter adapter;
    private File[] lists = new File[5];
    private File watchList;
    private File completedList;
    private File planToWatchList;
    private File onHoldList;
    private File droppedList;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        Toolbar toolbar = (Toolbar) findViewById(R.id.toolbar);
        setSupportActionBar(toolbar);

        FloatingActionButton fab = (FloatingActionButton) findViewById(R.id.addBtn);
        fab.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                startActivityForResult(new Intent(MainActivity.this, MovieForm.class), 1);
            }
        });

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        ActionBarDrawerToggle toggle = new ActionBarDrawerToggle(
                this, drawer, toolbar, R.string.navigation_drawer_open, R.string.navigation_drawer_close);
        drawer.addDrawerListener(toggle);
        toggle.syncState();

        NavigationView navigationView = (NavigationView) findViewById(R.id.nav_view);
        navigationView.setNavigationItemSelectedListener(this);

        initLists();
    }

    @Override
    public void onBackPressed() {
        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        if (drawer.isDrawerOpen(GravityCompat.START)) {
            drawer.closeDrawer(GravityCompat.START);
        } else {
            super.onBackPressed();
        }
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.main, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }

    @SuppressWarnings("StatementWithEmptyBody")
    @Override
    public boolean onNavigationItemSelected(MenuItem item) {
        // Handle navigation view item clicks here.
        int id = item.getItemId();

        rvMovies = (RecyclerView) findViewById(R.id.rvMovies);
        final List<MovieCard> moviesList = new ArrayList<>();

        DocumentBuilderFactory docBuilderFactory = DocumentBuilderFactory.newInstance();
        DocumentBuilder docBuilder;
        Document document = null;

        if (id == R.id.watching) {
            try {
                docBuilder = docBuilderFactory.newDocumentBuilder();
                document = docBuilder.parse(watchList);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.completed) {
            try {
                docBuilder = docBuilderFactory.newDocumentBuilder();
                document = docBuilder.parse(completedList);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.plan_to_watch) {
            try {
                docBuilder = docBuilderFactory.newDocumentBuilder();
                document = docBuilder.parse(planToWatchList);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.on_hold) {
            try {
                docBuilder = docBuilderFactory.newDocumentBuilder();
                document = docBuilder.parse(onHoldList);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        } else if (id == R.id.dropped) {
            try {
                docBuilder = docBuilderFactory.newDocumentBuilder();
                document = docBuilder.parse(droppedList);
            } catch (ParserConfigurationException e) {
                e.printStackTrace();
            } catch (IOException e) {
                e.printStackTrace();
            } catch (SAXException e) {
                e.printStackTrace();
            }
        }

        NodeList nodeList = document.getElementsByTagName("title");
        for (int i = 0; i < nodeList.getLength(); i++) {
            Node currentNode = nodeList.item(i);
            moviesList.add(new MovieCard(currentNode.getTextContent()));
        }

        adapter = new MovieAdapter(moviesList);
        rvMovies.setAdapter(adapter);
        rvMovies.setLayoutManager(new LinearLayoutManager((this)));

        DrawerLayout drawer = (DrawerLayout) findViewById(R.id.drawer_layout);
        drawer.closeDrawer(GravityCompat.START);
        return true;
    }

    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == 1) {
            if (resultCode == Activity.RESULT_OK) {
                HashMap<String, String> result = (HashMap<String, String>) data.getSerializableExtra("result");
                DocumentBuilderFactory documentBuilderFactory = DocumentBuilderFactory.newInstance();
                Document document = null;
                String listSelected = result.get("selection");
                File fileSelected = null;

                try {
                    DocumentBuilder documentBuilder = documentBuilderFactory.newDocumentBuilder();

                    if (listSelected.equals("Watching")) {
                        fileSelected = watchList;
                    } else if (listSelected.equals("Completed")) {
                        fileSelected = completedList;
                    } else if (listSelected.equals("Plan to Watch")) {
                        fileSelected = planToWatchList;
                    } else if (listSelected.equals("On Hold")) {
                        fileSelected = onHoldList;
                    } else if (listSelected.equals("Dropped")) {
                        fileSelected = droppedList;
                    }

                    document = documentBuilder.parse(fileSelected);

                    Element root = document.getDocumentElement();
                    Element movie = document.createElement("movie");

                    Element title = document.createElement("title");
                    title.appendChild(document.createTextNode(result.get("Title")));
                    movie.appendChild(title);

                    root.appendChild(movie);

                    DOMSource source = new DOMSource(document);

                    TransformerFactory transformerFactory = TransformerFactory.newInstance();
                    Transformer transformer = transformerFactory.newTransformer();
                    StreamResult streamResult = new StreamResult(fileSelected);
                    transformer.transform(source, streamResult);
                } catch (IOException e) {
                    e.printStackTrace();
                } catch (TransformerConfigurationException e) {
                    e.printStackTrace();
                } catch (ParserConfigurationException e) {
                    e.printStackTrace();
                } catch (SAXException e) {
                    e.printStackTrace();
                } catch (TransformerException e) {
                    e.printStackTrace();
                }
            }
            if (resultCode == Activity.RESULT_CANCELED) {
                //Log.d("tag", "Cancelled");
            }
        }
        //return to last state
    }

    private void createEmptyLists() throws IOException {

        FileOutputStream fos;
        StringWriter writer;

        for (File listFile: lists) {
            listFile.createNewFile();
            fos = new FileOutputStream(listFile);
            XmlSerializer xs = Xml.newSerializer();
            writer = new StringWriter();

            xs.setOutput(writer);
            xs.startDocument("UTF-8", true);
            xs.setFeature("http://xmlpull.org/v1/doc/features.html#indent-output", true);

            if (listFile.getAbsolutePath().equals(watchList.getAbsolutePath())) {
                xs.startTag("", "watchlist");
                xs.endTag("", "watchlist");
            } else if (listFile.getAbsolutePath().equals(completedList.getAbsolutePath())) {
                xs.startTag("", "completed_list");
                xs.endTag("", "completed_list");
            } else if (listFile.getAbsolutePath().equals(planToWatchList.getAbsolutePath())) {
                xs.startTag("", "plan_to_watch_list");
                xs.endTag("", "plan_to_watch_list");
            } else if (listFile.getAbsolutePath().equals(onHoldList.getAbsolutePath())) {
                xs.startTag("", "on_hold_list");
                xs.endTag("", "on_hold_list");
            } else if (listFile.getAbsolutePath().equals(droppedList.getAbsolutePath())) {
                xs.startTag("", "dropped_list");
                xs.endTag("", "dropped_list");
            }

            xs.endDocument();
            xs.flush();
            fos.write(writer.toString().getBytes());
            writer.close();
        }
    }

    private void initLists() {
        watchList = new File(getApplicationContext().getFilesDir(), "watchlist.xml");
        completedList = new File(getApplicationContext().getFilesDir(), "completed_list.xml");
        planToWatchList = new File(getApplicationContext().getFilesDir(), "plan_to_watch_list.xml");
        onHoldList = new File(getApplicationContext().getFilesDir(), "on_hold_list.xml");
        droppedList = new File(getApplicationContext().getFilesDir(), "dropped_list.xml");

        lists[0] = watchList;
        lists[1] = completedList;
        lists[2] = planToWatchList;
        lists[3] = onHoldList;
        lists[4] = droppedList;

        //for testing -- remove later
        watchList.delete();
        completedList.delete();
        planToWatchList.delete();
        onHoldList.delete();
        droppedList.delete();

        if (!watchList.exists()) {
            try {
                createEmptyLists();
                } catch (IOException e) {
                    e.printStackTrace();
                }
        } else {
            //Log.d("tag", "File exists");
        }
    }
}
