package br.gov.mec.aghu.core.saucer;

import org.xhtmlrenderer.extend.ReplacedElement;
import org.xhtmlrenderer.render.BlockBox;
import org.xhtmlrenderer.render.RenderingContext;

public interface AGHUITextReplacedElement extends ReplacedElement
{
    public void paint(RenderingContext c, AGHUITextOutputDevice outputDevice, BlockBox box);
}