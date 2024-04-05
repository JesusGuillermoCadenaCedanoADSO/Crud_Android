package com.example.gdg_sqlitedatabase_demo

import android.app.AlertDialog
import android.content.ContentValues
import android.content.DialogInterface
import android.database.Cursor
import android.database.sqlite.SQLiteDatabase
import android.os.Bundle
import android.util.Log
import android.view.ContextMenu
import android.view.MenuItem
import android.view.View
import android.widget.Button
import android.widget.EditText
import android.widget.ListView
import android.widget.SearchView
import android.widget.SimpleCursorAdapter
import android.widget.Toast
import androidx.activity.ComponentActivity
import androidx.activity.compose.setContent
import androidx.compose.foundation.layout.fillMaxSize
import androidx.compose.material3.MaterialTheme
import androidx.compose.material3.Surface
import androidx.compose.material3.Text
import androidx.compose.runtime.Composable
import androidx.compose.ui.Modifier
import androidx.compose.ui.tooling.preview.Preview
import androidx.compose.ui.window.Dialog
import com.example.gdg_sqlitedatabase_demo.ui.theme.GDG_SQLiteDatabase_DemoTheme

class MainActivity : ComponentActivity() {
    lateinit var db:SQLiteDatabase
    lateinit var rs:Cursor
    lateinit var adapter: SimpleCursorAdapter

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        setContentView(R.layout.activity_main)
        /*setContent {
            GDG_SQLiteDatabase_DemoTheme {

                Surface(
                    modifier = Modifier.fillMaxSize(),
                    color = MaterialTheme.colorScheme.background
                ) {
                    Greeting("Android")
                }
            }
        }*/

        var helper=MyHelper(applicationContext)
        db=helper.readableDatabase
        rs=db.rawQuery("SELECT * FROM ACTABLE ORDER BY NAME",null)

        val edName=findViewById<EditText>(R.id.edName)
        val edMeaning=findViewById<EditText>(R.id.edMeaning)
        val btFirst=findViewById<Button>(R.id.btFirst)
        val btLast=findViewById<Button>(R.id.btLast)
        val btNext=findViewById<Button>(R.id.btNext)
        val btPrev=findViewById<Button>(R.id.btPrev)
        val btInsert=findViewById<Button>(R.id.btInsert)
        val btClear=findViewById<Button>(R.id.btClear)
        val btUpdate=findViewById<Button>(R.id.btUpdate)
        val btDelete=findViewById<Button>(R.id.btDelete)
        val btViewAll=findViewById<Button>(R.id.btViewAll)
        val searchView=findViewById<SearchView>(R.id.searchView)
        val listview=findViewById<ListView>(R.id.listview)

        adapter = SimpleCursorAdapter(applicationContext,android.R.layout.simple_expandable_list_item_2,rs,
            arrayOf("NAME","MEANING"),
            intArrayOf(android.R.id.text1,android.R.id.text2),0
        )
        listview.adapter=adapter

        registerForContextMenu(listview)

        btViewAll.setOnClickListener {
            adapter.notifyDataSetChanged()
            searchView.isIconified=false
            searchView.queryHint = "Search Among ${rs.count} Record"
            searchView.visibility= View.VISIBLE
            listview.visibility=View.VISIBLE
        }

        searchView.setOnQueryTextListener(object:SearchView.OnQueryTextListener{
            override fun onQueryTextSubmit(p0: String?): Boolean {
                return false
            }

            override fun onQueryTextChange(p0: String?): Boolean {
                rs = db.rawQuery("SELECT * FROM ACTABLE WHERE NAME LIKE '%${p0}%' OR MEANING LIKE '%${p0}%'",null)
                adapter.changeCursor(rs)
                return false
            }

        })


        btDelete.setOnClickListener {
            db.delete("ACTABLE","_id=?", arrayOf(rs.getString(0)))
            rs.requery()
            var ad= AlertDialog.Builder(this)
            ad.setTitle("Delete Record")
            ad.setMessage("Record Deleted Successfully...!");
            ad.setPositiveButton("OK", DialogInterface.OnClickListener{
                    dialogInterface, i->
                if(rs.moveToFirst()){
                    edName.setText(rs.getString(1))
                    edMeaning.setText(rs.getString(2))
                }
                else{
                    edName.setText("No Data Found")
                    edMeaning.setText("No Data Found")
                }
            }
            )
            ad.show()

        }


        btUpdate.setOnClickListener {
            var cv = ContentValues()
            cv.put("NAME",edName.text.toString())
            cv.put("MEANING",edMeaning.text.toString())
            db.update("ACTABLE",cv,"_id=?", arrayOf(rs.getString(0)))
            rs.requery()


            var ad= AlertDialog.Builder(this)
            ad.setTitle("Update Record")
            ad.setMessage("Record Updated Successfully...!");
            ad.setPositiveButton("OK", DialogInterface.OnClickListener{
                    dialogInterface, i->
                if(rs.moveToFirst()){
                    edName.setText(rs.getString(1))
                    edMeaning.setText(rs.getString(2))
                }
            }
            )
            ad.show()
        }


        btInsert.setOnClickListener {
            var cv = ContentValues()
            cv.put("NAME",edName.text.toString())
            cv.put("MEANING",edMeaning.text.toString())
            db.insert("ACTABLE",null,cv)
            rs.requery()

            var ad= AlertDialog.Builder(this)
            ad.setTitle("Add Record")
            ad.setMessage("Record Inserted Successfully...!");
            ad.setPositiveButton("OK", DialogInterface.OnClickListener{
                    dialogInterface, i->
                    edName.setText("")
                    edMeaning.setText("")
                    edName.requestFocus()
            }
            )
            ad.show()
        }


        btClear.setOnClickListener {
            edName.setText("")
            edMeaning.setText("")
            edName.requestFocus()
        }


        btFirst.setOnClickListener {
            if (rs.moveToFirst()) {
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            } else {
                Toast.makeText(applicationContext, "No Data Found", Toast.LENGTH_LONG).show()
            }
        }


        btLast.setOnClickListener {
            if (rs.moveToLast()) {
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            } else {
                Toast.makeText(applicationContext, "No Data Found", Toast.LENGTH_LONG).show()
            }
        }


        btNext.setOnClickListener {
                if (rs.moveToNext()) {
                    edName.setText(rs.getString(1))
                    edMeaning.setText(rs.getString(2))
                }
                else if(rs.moveToFirst()){
                    edName.setText(rs.getString(1))
                    edMeaning.setText(rs.getString(2))
                }else {
                    Toast.makeText(applicationContext, "No Data Found", Toast.LENGTH_LONG).show()
                }
        }


        btPrev.setOnClickListener {
            if (rs.moveToPrevious()) {
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            }
            else if(rs.moveToLast()){
                edName.setText(rs.getString(1))
                edMeaning.setText(rs.getString(2))
            }else {
                Toast.makeText(applicationContext, "No Data Found", Toast.LENGTH_LONG).show()
            }
        }
    }


    override fun onCreateContextMenu(
        menu: ContextMenu?,
        v: View?,
        menuInfo: ContextMenu.ContextMenuInfo?
    ) {
        super.onCreateContextMenu(menu, v, menuInfo)
        menu?.add(101,11,1,"DELETE")
        menu?.setHeaderTitle("Removing Data")
    }


    override fun onContextItemSelected(item: MenuItem): Boolean {
        val searchView=findViewById<SearchView>(R.id.searchView)
        if(item.itemId==11)
        {
            db.delete("ACTABLE","_id=?", arrayOf(rs.getString(0)))
            rs.requery()
            adapter.notifyDataSetChanged()
            searchView.queryHint = "Search Among ${rs.count} Record"
            //Toast.makeText(applicationContext,"You clicked on Delete Context Menu",Toast.LENGTH_LONG).show()
        }
        return super.onContextItemSelected(item)
    }
}

@Composable
fun Greeting(name: String, modifier: Modifier = Modifier) {
    Text(
        text = "Hello $name!",
        modifier = modifier
    )
}

@Preview(showBackground = true)
@Composable
fun GreetingPreview() {
    GDG_SQLiteDatabase_DemoTheme {
        Greeting("Android")
    }
}