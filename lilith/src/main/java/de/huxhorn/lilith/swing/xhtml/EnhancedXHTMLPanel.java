/*
 * Lilith - a log event viewer.
 * Copyright (C) 2007-2011 Joern Huxhorn
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
// ##### below code didn't work as expected anyway and prevented proper use of Mensch font...
// ##### for some mysterious reason...
//package de.huxhorn.lilith.swing.xhtml;
//
//import org.xhtmlrenderer.simple.XHTMLPanel;
//import org.xhtmlrenderer.render.Box;
//import org.xhtmlrenderer.css.style.derived.RectPropertySet;
//import org.w3c.dom.Document;
//import org.slf4j.Logger;
//import org.slf4j.LoggerFactory;
//
//import java.awt.*;
//
//public class EnhancedXHTMLPanel
//	extends XHTMLPanel
//{
//	private final Logger logger = LoggerFactory.getLogger(EnhancedXHTMLPanel.class);
//
//	public void setDocumentRelative(String filename)
//	{
//		String url = getSharedContext().getUac().resolveURI(filename);
//		if (isAnchorInCurrentDocument(filename))
//		{
//			if (logger.isDebugEnabled()) logger.debug("isAnchor");
//			String id = getAnchorId(filename);
//			Box box = getSharedContext().getBoxById(id);
//			if (logger.isDebugEnabled()) logger.debug("Box: {}", box);
//			if (box != null)
//			{
//				Point pt;
//				if (box.getStyle().isInline())
//				{
//					pt = new Point(box.getAbsX(), box.getAbsY());
//				}
//				else
//				{
//					RectPropertySet margin = box.getMargin(getLayoutContext());
//					pt = new Point(
//							box.getAbsX() + (int) margin.left(),
//							box.getAbsY() + (int) margin.top());
//				}
//				if (logger.isDebugEnabled()) logger.debug("Point: {}", pt);
//				scrollTo(pt);
//				return;
//			}
//		}
//		Document dom = loadDocument(url);
//		setDocument(dom, url);
//	}
//
//	private boolean isAnchorInCurrentDocument(String str)
//	{
//		return str.charAt(0) == '#';
//	}
//
//	private String getAnchorId(String url)
//	{
//		return url.substring(1, url.length());
//	}
//
//}
