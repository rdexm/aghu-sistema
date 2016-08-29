package br.gov.mec.aghu.faturamento.business;

import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.beanutils.BeanUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatCompatExclusItemDAO;
import br.gov.mec.aghu.model.FatCompatExclusItem;
import br.gov.mec.aghu.model.FatItensProcedHospitalar;
import br.gov.mec.aghu.model.FatItensProcedHospitalarId;
import br.gov.mec.aghu.model.FatTipoTransplante;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.BaseException;

@Stateless
public class FatCompatExclusItemON extends BaseBusiness {


@EJB
private FatCompatExclusItemRN fatCompatExclusItemRN;

private static final Log LOG = LogFactory.getLog(FatCompatExclusItemON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private FatCompatExclusItemDAO fatCompatExclusItemDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 6065499609494915667L;

	/** Clona a lista FatCompatExclusItem
	 * 
	 * @param listaOriginal
	 * @return
	 */
	@SuppressWarnings("PMD.SignatureDeclareThrowsException")
	public List<FatCompatExclusItem> clonarListaFatCompatExclusItem(
			List<FatCompatExclusItem> listaOriginal) throws Exception {
		List<FatCompatExclusItem> listaClonada = new ArrayList<FatCompatExclusItem>();
		
		for (FatCompatExclusItem elemento : listaOriginal) {
			FatCompatExclusItem clone = (FatCompatExclusItem) BeanUtils.cloneBean(elemento);
			if (elemento.getItemProcedHosp() != null) {
				FatItensProcedHospitalar itemProcedHosp = new FatItensProcedHospitalar();
				FatItensProcedHospitalarId id = new FatItensProcedHospitalarId();
				id.setPhoSeq(elemento.getItemProcedHosp().getId().getPhoSeq());
				id.setSeq(elemento.getItemProcedHosp().getId().getSeq());
				itemProcedHosp.setId(id);
				
				clone.setItemProcedHosp(itemProcedHosp);
			}
			
			if (elemento.getItemProcedHospCompatibiliza() != null) {
				FatItensProcedHospitalar itemProcedHospCompat = new FatItensProcedHospitalar();
				FatItensProcedHospitalarId id = new FatItensProcedHospitalarId();
				id.setPhoSeq(elemento.getItemProcedHospCompatibiliza().getId().getPhoSeq());
				id.setSeq(elemento.getItemProcedHospCompatibiliza().getId().getSeq());
				itemProcedHospCompat.setId(id);
				
				clone.setItemProcedHospCompatibiliza(itemProcedHospCompat);
			}
			
			if (elemento.getTipoTransplante() != null) {
				FatTipoTransplante tipoTransplante = new FatTipoTransplante();
				tipoTransplante.setCodigo(elemento.getTipoTransplante().getCodigo());
				clone.setTipoTransplante(tipoTransplante);
			}
			
			listaClonada.add(clone);
		}
		
		return listaClonada;
	}
	
	
	public void persistirFatCompatExclusItem(final FatCompatExclusItem elemento) throws BaseException {
		
		if (elemento.getSeq() == null) {//insert
			fatCompatExclusItemRN.executarAntesInserirFatCompatExclusItem(elemento);
			fatCompatExclusItemDAO.persistir(elemento);
			
		} else { //update
			final FatCompatExclusItem old = fatCompatExclusItemDAO.obterOriginal(elemento);
			
			fatCompatExclusItemRN.executarAntesAtualizarFatCompatExclusItem(elemento);
			fatCompatExclusItemDAO.merge(elemento);

			fatCompatExclusItemRN.executarAposAtualizarFatCompatExclusItem(old, elemento);
		}
	}
	
	
	
	/**
	 * Persiste Cadastro de Compatibilidade
	 * 
	 * @param newListaFatCompatExclusItem
	 * @param oldListaFatCompatExclusItem
	 * @param excluidosListaFatCompatExclusItem
	 * @throws BaseException
	 */
	public void persistirFatCompatExclusItem(List<FatCompatExclusItem> newListaFatCompatExclusItem, 
			List<FatCompatExclusItem> oldListaFatCompatExclusItem, 
			List<FatCompatExclusItem> excluidosListaFatCompatExclusItem) throws BaseException {
		FatCompatExclusItemRN fatCompatExclusItemRN = getFatCompatExclusItemRN();
		FatCompatExclusItemDAO dao = getFatCompatExclusItemDAO();
		
		for (FatCompatExclusItem elemento : newListaFatCompatExclusItem) {
			
			if (elemento.getModificado() != null && elemento.getModificado().booleanValue()) { //se foi modificado persiste
				
				if (elemento.getSeq() == null) {//insert
					fatCompatExclusItemRN.executarAntesInserirFatCompatExclusItem(elemento);
					dao.persistir(elemento);
					dao.flush();
				}
				else { //update
					fatCompatExclusItemRN.executarAntesAtualizarFatCompatExclusItem(elemento);
					dao.atualizar(elemento);
					dao.flush();
					fatCompatExclusItemRN.executarAposAtualizarFatCompatExclusItem(buscaElementoOld(oldListaFatCompatExclusItem, elemento), elemento);
				}
			}
		}
		
		for (FatCompatExclusItem elemento : excluidosListaFatCompatExclusItem) {
			removerFatCompatExclusItem(elemento.getSeq());
		}
	}
	
	/** Exclui cadastro de compatiblidade
	 * 
	 * @param fatCompatExclusItem
	 */
	public void removerFatCompatExclusItem(Long seq) {
		FatCompatExclusItemDAO dao = getFatCompatExclusItemDAO();
		FatCompatExclusItem fatCompatExclusItem = dao.obterPorChavePrimaria(seq);
		getFatCompatExclusItemDAO().remover(fatCompatExclusItem);
		getFatCompatExclusItemDAO().flush();
		getFatCompatExclusItemRN().executarAposDeletarFatCompatExclusItem(fatCompatExclusItem);
	}

	private FatCompatExclusItem buscaElementoOld(List<FatCompatExclusItem> oldListaFatCompatExclusItem, FatCompatExclusItem elemento) {
		for (FatCompatExclusItem elementoOld : oldListaFatCompatExclusItem) {
			if (elementoOld.getSeq().equals(elemento.getSeq())) {
				return elementoOld;
			}
		}
		return null;
	}
	
	protected FatCompatExclusItemDAO getFatCompatExclusItemDAO() {
		return fatCompatExclusItemDAO;
	}	
	
	protected FatCompatExclusItemRN getFatCompatExclusItemRN() {
		return fatCompatExclusItemRN;
	}	
}
