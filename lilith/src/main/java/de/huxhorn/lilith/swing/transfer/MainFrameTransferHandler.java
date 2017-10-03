/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2017 Joern Huxhorn
 *
 * This program is free software: you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation, either version 3 of the License, or
 * (at your option) any later version.
 *
 * This program is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with this program.  If not, see <http://www.gnu.org/licenses/>.
 */

package de.huxhorn.lilith.swing.transfer;

import de.huxhorn.lilith.api.FileConstants;
import de.huxhorn.lilith.swing.MainFrame;
import java.awt.datatransfer.DataFlavor;
import java.awt.datatransfer.Transferable;
import java.awt.datatransfer.UnsupportedFlavorException;
import java.io.File;
import java.io.IOException;
import java.util.List;
import java.util.Locale;
import javax.swing.JComponent;
import javax.swing.JDesktopPane;
import javax.swing.TransferHandler;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

public final class MainFrameTransferHandler
	extends TransferHandler
{
	private static final long serialVersionUID = 6201602937026372558L;
	private final Logger logger = LoggerFactory.getLogger(MainFrameTransferHandler.class);

	private final MainFrame mainFrame;
	private final JDesktopPane desktop;

	public MainFrameTransferHandler(MainFrame mainFrame)
	{
		this.mainFrame = mainFrame;
		this.desktop = mainFrame.getDesktop();
	}

	public void attach()
	{
		this.desktop.setTransferHandler(this);
		if(logger.isDebugEnabled()) logger.debug("Attached transfer handler to desktop.");
	}

	@Override
	public boolean importData(JComponent comp, Transferable transferable)
	{
		return canImport(comp, transferable.getTransferDataFlavors()) && importData(transferable);
	}

	@Override
	public boolean canImport(JComponent comp,
	                         DataFlavor[] transferFlavors)
	{
		if(comp != desktop) // NOPMD
		{
			return false;
		}
		if(transferFlavors != null)
		{
			for(DataFlavor current : transferFlavors)
			{
				if(DataFlavor.javaFileListFlavor.equals(current))
				{
					return true;
				}
			}
		}
		return false;
	}

	private boolean importData(Transferable transferable)
	{
		if(!transferable.isDataFlavorSupported(DataFlavor.javaFileListFlavor))
		{
			return false;
		}

		try
		{
			List l = (List) transferable.getTransferData(DataFlavor.javaFileListFlavor);

			for(Object o : l)
			{
				if(o instanceof File)
				{
					File file = (File) o;
					String fileName = file.getAbsolutePath();
					if(logger.isDebugEnabled()) logger.debug("Checking file '{}'...", fileName);
					if(fileName.toLowerCase(Locale.US).endsWith(FileConstants.FILE_EXTENSION))
					{
						mainFrame.open(file);
					}
				}

			}
		}
		catch(UnsupportedFlavorException | IOException e)
		{
			return false;
		}

		return true;
	}

	@Override
	public boolean canImport(TransferHandler.TransferSupport support)
	{
		return support.isDataFlavorSupported(DataFlavor.javaFileListFlavor)
				&& (COPY & support.getSourceDropActions()) != 0;
	}

	@Override
	public boolean importData(TransferHandler.TransferSupport support)
	{
		if(!canImport(support))
		{
			return false;
		}

		Transferable t = support.getTransferable();
		return importData(t);
	}
}
