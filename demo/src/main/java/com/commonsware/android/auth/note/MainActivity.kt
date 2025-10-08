/***
 * Copyright (c) 2018 CommonsWare, LLC
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy
 * of the License at http://www.apache.org/licenses/LICENSE-2.0. Unless required
 * by applicable law or agreed to in writing, software distributed under the
 * License is distributed on an "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS
 * OF ANY KIND, either express or implied. See the License for the specific
 * language governing permissions and limitations under the License.
 *
 * Covered in detail in the book _The Busy Coder's Guide to Android Development_
 * https://commonsware.com/Android
 */

package com.commonsware.android.auth.note

import android.app.KeyguardManager
import android.content.Intent
import android.os.Bundle
import android.util.Log
import android.view.Menu
import android.view.MenuItem
import android.widget.Toast
import androidx.appcompat.app.AppCompatActivity
import androidx.lifecycle.ViewModelProvider
import com.commonsware.android.auth.note.databinding.ActivityMainBinding

private const val REQUEST_SAVE = 1337
private const val REQUEST_LOAD = 1338

class MainActivity : AppCompatActivity() {
    private lateinit var mgr: KeyguardManager
    private lateinit var viewmodel: NoteViewModel
    private lateinit var binding: ActivityMainBinding

    override fun onCreate(savedInstanceState: Bundle?) {
        super.onCreate(savedInstanceState)
        mgr = getSystemService(KEYGUARD_SERVICE) as KeyguardManager
        if (mgr.isKeyguardSecure) {
            binding = ActivityMainBinding.inflate(layoutInflater)
            setContentView(binding.root)
            viewmodel = ViewModelProvider(this)[NoteViewModel::class.java]
            viewmodel.notes.observe(this) { note -> binding.textarea.setText(note?.content) }
            viewmodel.authEvents.observeEvent(this) { case -> handleAuthEvent(case) }
            viewmodel.problems.observeEvent(this) { t -> handleProblem(t) }
            viewmodel.savedEvents.observeEvent(this) {
                Toast.makeText(this, R.string.saved, Toast.LENGTH_LONG).show()
            }
            viewmodel.load()
        } else {
            Toast.makeText(this, R.string.insecure, Toast.LENGTH_LONG).show()
            finish()
        }
    }

    override fun onCreateOptionsMenu(menu: Menu): Boolean {
        menuInflater.inflate(R.menu.actions, menu)
        return super.onCreateOptionsMenu(menu)
    }

    override fun onOptionsItemSelected(item: MenuItem): Boolean {
        if (item.itemId == R.id.save) {
            viewmodel.save(binding.textarea.text.toString())
            return true
        }
        return super.onOptionsItemSelected(item)
    }

    override fun onActivityResult(requestCode: Int, resultCode: Int, data: Intent?) {
        if (resultCode == RESULT_OK) {
            if (requestCode == REQUEST_SAVE) {
                viewmodel.save(binding.textarea.text.toString())
            } else if (requestCode == REQUEST_LOAD) {
                viewmodel.load()
            }
        } else {
            Toast.makeText(this, R.string.sorry, Toast.LENGTH_SHORT).show()
            finish()
        }
        if (requestCode != REQUEST_SAVE && requestCode != REQUEST_LOAD) {
            super.onActivityResult(requestCode, resultCode, data)
        }
    }

    @Suppress("DEPRECATION")
    private fun handleAuthEvent(event: AuthCase) {
        val i = mgr.createConfirmDeviceCredentialIntent("Note", "Please unlock to proceed")
        if (i == null) {
            Toast.makeText(this, "No authentication required?!?", Toast.LENGTH_SHORT).show()
        } else {
            val requestCode: Int = when (event) {
                AuthCase.RETRY_LOAD -> REQUEST_LOAD
                AuthCase.RETRY_SAVE -> REQUEST_SAVE
            }
            startActivityForResult(i, requestCode)
        }
    }

    private fun handleProblem(t: Throwable) {
        Toast.makeText(this@MainActivity, t.message, Toast.LENGTH_LONG).show()
        Log.e(getString(R.string.app_name), "Exception loading encrypted file", t)
    }
}
