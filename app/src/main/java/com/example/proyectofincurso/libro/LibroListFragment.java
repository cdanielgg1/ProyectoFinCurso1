package com.example.proyectofincurso.libro;

import android.content.Context;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.support.v4.app.ListFragment;
import android.support.v4.app.LoaderManager;
import android.support.v4.content.CursorLoader;
import android.support.v4.content.Loader;
import android.support.v4.widget.CursorAdapter;
import android.util.Log;
import android.view.ActionMode;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ImageView;
import android.widget.TextView;

import com.amulyakhare.textdrawable.TextDrawable;
import com.amulyakhare.textdrawable.util.ColorGenerator;
import com.example.proyectofincurso.R;
import com.example.proyectofincurso.constantes.G;
import com.example.proyectofincurso.constantes.Utilidades;
import com.example.proyectofincurso.pojos.Libro;
import com.example.proyectofincurso.proveedor.Contrato;
import com.example.proyectofincurso.proveedor.LibroProveedor;

import java.io.FileNotFoundException;

public class LibroListFragment extends ListFragment
		implements LoaderManager.LoaderCallbacks<Cursor> {
	
	//private static final String LOGTAG = "Tiburcio -  LibroListFragment";

	LibroCursorAdapter mAdapter;
	LoaderManager.LoaderCallbacks<Cursor> mCallbacks;


	//variable para consultar si el action mode esta activo

	ActionMode mActionMode;

	//variable View seleccionado para borrar o editar
	View viewSeleccionado;

	public static  LibroListFragment newInstance() {
		LibroListFragment f = new  LibroListFragment();

		return f;
	}

	/**
	 * When creating, retrieve this instance's number from its arguments.
	 */

	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);

		setHasOptionsMenu(true);

	}

	@Override
	public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
		MenuItem menuItem = menu.add(Menu.NONE, G.INSERTAR, Menu.NONE, "Insertar");
		menuItem.setIcon(R.drawable.ic_add_black_24dp);
		menuItem.setShowAsAction(MenuItem.SHOW_AS_ACTION_ALWAYS);


		super.onCreateOptionsMenu(menu, inflater);
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item) {

		switch (item.getItemId()){

			case G.INSERTAR:
				Intent intent = new Intent(getActivity(), LibroInsertarActivity.class);
				startActivity(intent);
				break;

		}



		return super.onOptionsItemSelected(item);
	}

	/**
	 * The Fragment's UI is just a simple text view showing its
	 * instance number.
	 */



	@Override
	public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
		//Log.i(LOGTAG, "onCreateView");
		View v = inflater.inflate(R.layout.fragment_libro_list, container, false);

		mAdapter = new LibroCursorAdapter(getActivity());
		setListAdapter(mAdapter);

		return v;
	}
	@Override
	public void onActivityCreated(Bundle savedInstanceState) {
		super.onActivityCreated(savedInstanceState);
		//Log.i(LOGTAG, "onActivityCreated");

		mCallbacks = this;

		getLoaderManager().initLoader(0, null, mCallbacks);

		getListView().setOnItemLongClickListener(new AdapterView.OnItemLongClickListener() {
			@Override
			public boolean onItemLongClick(AdapterView<?> parent, View view, int position, long id) {
				if(mActionMode!=null){
					return false;
				}
				mActionMode = getActivity().startActionMode(mActionModeCallback);
				view.setSelected(true);
				viewSeleccionado = view;
				return true;
			}
		});
	}

	ActionMode.Callback mActionModeCallback = new ActionMode.Callback(){

		@Override
		public boolean onCreateActionMode(ActionMode mode, Menu menu) {
			MenuInflater inflater = mode.getMenuInflater();
			inflater.inflate(R.menu.menu_contextual, menu);
			return true;
		}

		@Override
		public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
			return false;
		}

		@Override
		public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
			switch(item.getItemId()){
				case R.id.menu_borrar:
					int libroId = (Integer) viewSeleccionado.getTag();
					LibroProveedor.deleteRecord(getActivity().getContentResolver(), libroId);
					break;
				case R.id.menu_editar:
					Intent intent = new Intent(getActivity(), LibroModificarActivity.class);
					libroId = (Integer) viewSeleccionado.getTag();
					Log.i("El identificador 1", "kk"+libroId);
					Libro libro = LibroProveedor.readRecord(getActivity().getContentResolver(), libroId);
					Log.i("El identificador", libro.getTitulo());
					intent.putExtra("ID", libro.getID());
					intent.putExtra("Titulo", libro.getTitulo());
					intent.putExtra("Paginas", libro.getPaginas());
					startActivity(intent);
					break;
				default:
					return false;
			}
			mode.finish();
			return true;
		}

		@Override
		public void onDestroyActionMode(ActionMode mode) {
			mActionMode = null;
		}
	};


	public Loader<Cursor> onCreateLoader(int id, Bundle args) {
		// This is called when a new Loader needs to be created.  This
		// sample only has one Loader, so we don't care about the ID.
		// First, pick the base URI to use depending on whether we are
		// currently filtering.
		String columns[] = new String[] { Contrato.Libro._ID,
										  Contrato.Libro.TITULO,
				                          Contrato.Libro.PAGINAS
										};

		Uri baseUri = Contrato.Libro.CONTENT_URI;

		// Now create and return a CursorLoader that will take care of
		// creating a Cursor for the data being displayed.

		String selection = null;

		return new CursorLoader(getActivity(), baseUri,
				columns, selection, null, null);
	}

	public void onLoadFinished(Loader<Cursor> loader, Cursor data) {
		// Swap the new cursor in.  (The framework will take care of closing the
		// old cursor once we return.)

		Uri laUriBase = Uri.parse("content://"+Contrato.AUTHORITY+"/Libro");
		data.setNotificationUri(getActivity().getContentResolver(), laUriBase);
		
		mAdapter.swapCursor(data);
	}

	public void onLoaderReset(Loader<Cursor> loader) {
		// This is called when the last Cursor provided to onLoadFinished()
		// above is about to be closed.  We need to make sure we are no
		// longer using it.
		mAdapter.swapCursor(null);
	}

	public class LibroCursorAdapter extends CursorAdapter {
		public LibroCursorAdapter(Context context) {
			super(context, null, false);
		}

		@Override
		public void bindView(View view, Context context, Cursor cursor) {
			int ID = cursor.getInt(cursor.getColumnIndex(Contrato.Libro._ID));
			String titulo = cursor.getString(cursor.getColumnIndex(Contrato.Libro.TITULO));
			int paginas = cursor.getInt(cursor.getColumnIndex(Contrato.Libro.PAGINAS));
			String paginasString = String.valueOf(paginas);

			TextView textviewTitulo = (TextView) view.findViewById(R.id.textview_libro_list_item_titulo);
			textviewTitulo.setText(titulo);

			TextView textviewPaginas = (TextView) view.findViewById(R.id.textview_libro_list_item_paginas);
			textviewPaginas.setText(paginasString);

			ImageView image = (ImageView) view.findViewById(R.id.image_view);

			try {
				Utilidades.loadImageFromStorage(getActivity(), "img_" + ID + ".jpg", image);
			} catch (FileNotFoundException e) {
				ponerImagenDeLetra(paginasString, image);
			}

			view.setTag(ID);

		}

		private void ponerImagenDeLetra(String abreviatura, ImageView image) {
			ColorGenerator generator = ColorGenerator.MATERIAL; // or use DEFAULT
			int color = generator.getColor(abreviatura); //Genera un color según el nombre
			TextDrawable drawable = TextDrawable.builder()
					.buildRound(abreviatura.substring(0, 1), color);
			image.setImageDrawable(drawable);
		}

		@Override
		public View newView(Context context, Cursor cursor, ViewGroup parent) {
			LayoutInflater inflater = LayoutInflater.from(context);
			View v = inflater.inflate(R.layout.libro_list_item, parent, false);
			bindView(v, context, cursor);
			return v;
		}
	}
}
