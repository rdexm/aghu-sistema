package br.gov.mec.aghu.transplante.business;

import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.TreeSet;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.MtxItemPeriodoRetorno;
import br.gov.mec.aghu.model.MtxPeriodoRetorno;
import br.gov.mec.aghu.model.MtxTipoRetorno;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.transplante.dao.MtxItemPeriodoRetornoDAO;
import br.gov.mec.aghu.transplante.dao.MtxPeriodoRetornoDAO;
import br.gov.mec.aghu.transplante.dao.MtxTipoRetornoDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class MtxPeriodoRetornoRN extends BaseBusiness {
	
	private static final long serialVersionUID = -5553288417265871288L;
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@Inject
	private MtxPeriodoRetornoDAO mtxPeriodoRetornoDAO;
	@Inject
	private MtxTipoRetornoDAO mtxTipoRetornoDAO;
	
	@Inject
	private MtxItemPeriodoRetornoDAO mtxItemPeriodoRetornoDAO;

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void persistirPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno,MtxTipoRetorno selecionaDescricao, DominioSimNao dominioSimNao,List<MtxItemPeriodoRetorno> listaItem) {
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MtxTipoRetorno tipoRetorno = mtxTipoRetornoDAO.obterPorChavePrimaria(selecionaDescricao.getSeq());
		mtxPeriodoRetorno.setServidor(servidorLogado);
		mtxPeriodoRetorno.setTipoRetorno(tipoRetorno);
		mtxPeriodoRetorno.setCriadoEm(new Date());
		mtxPeriodoRetorno.setIndSituacao(dominioSimNao != null ? DominioSituacao.getInstance(dominioSimNao.isSim()) : null);
		
		mtxPeriodoRetornoDAO.persistir(mtxPeriodoRetorno);
		mtxPeriodoRetorno = mtxPeriodoRetornoDAO.obterPorChavePrimaria(mtxPeriodoRetorno.getSeq());
		
		for (MtxItemPeriodoRetorno mtxItemPeriodoRetorno : listaItem) {
			mtxItemPeriodoRetorno.setPeriodoRetorno(mtxPeriodoRetorno);
		}
			
		Set<MtxItemPeriodoRetorno> setLista = new TreeSet<MtxItemPeriodoRetorno>();
		setLista.addAll(listaItem);
		mtxPeriodoRetorno.setListaItemPeriodoRetorno(setLista);
		
		for (MtxItemPeriodoRetorno mtxItemPeriodoRetorno : listaItem) {
			mtxItemPeriodoRetornoDAO.persistir(mtxItemPeriodoRetorno);
		}	
	}
	
	public void editarPeriodoRetorno(MtxPeriodoRetorno mtxPeriodoRetorno,MtxTipoRetorno selecionaDescricao, DominioSimNao dominioSimNao,List<MtxItemPeriodoRetorno> listaItem, List<MtxItemPeriodoRetorno> listaItemExcluir) {
		mtxPeriodoRetorno = mtxPeriodoRetornoDAO.obterPorChavePrimaria(mtxPeriodoRetorno.getSeq());
		RapServidores servidorLogado = servidorLogadoFacade.obterServidorLogado();
		MtxTipoRetorno tipoRetorno = mtxTipoRetornoDAO.obterPorChavePrimaria(selecionaDescricao.getSeq());
		mtxPeriodoRetorno.setServidor(servidorLogado);
		mtxPeriodoRetorno.setTipoRetorno(tipoRetorno);
		mtxPeriodoRetorno.setCriadoEm(new Date());
		mtxPeriodoRetorno.setIndSituacao(dominioSimNao != null ? DominioSituacao.getInstance(dominioSimNao.isSim()) : null);
		mtxPeriodoRetornoDAO.merge(mtxPeriodoRetorno);
		mtxPeriodoRetornoDAO.flush();
		
		if(!listaItemExcluir.isEmpty()){
			for (MtxItemPeriodoRetorno mtxItemPeriodoRetorno : listaItemExcluir) {
				if(mtxItemPeriodoRetorno.getSeq() != null){
					MtxItemPeriodoRetorno itemTipoRetorno = mtxItemPeriodoRetornoDAO.obterPorChavePrimaria(mtxItemPeriodoRetorno.getSeq());
					mtxItemPeriodoRetornoDAO.remover(itemTipoRetorno);
				}			
			}
			flush();
		}
				
		for (MtxItemPeriodoRetorno mtxItemPeriodoRetorno : listaItem) {
			if(mtxItemPeriodoRetorno.getSeq() != null) {
				MtxItemPeriodoRetorno itemPeriodo = mtxItemPeriodoRetornoDAO.obterPorChavePrimaria(mtxItemPeriodoRetorno.getSeq());
				itemPeriodo.setQuantidade(mtxItemPeriodoRetorno.getQuantidade());
				itemPeriodo.setOrdem(mtxItemPeriodoRetorno.getOrdem());
				mtxItemPeriodoRetornoDAO.merge(itemPeriodo);
			}else {
				mtxItemPeriodoRetorno.setPeriodoRetorno(mtxPeriodoRetorno);
				mtxItemPeriodoRetornoDAO.persistir(mtxItemPeriodoRetorno);
			}
		}
		flush();
		
	}
	
	
	
	
	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
	
	

}
