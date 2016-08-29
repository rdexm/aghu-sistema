package br.gov.mec.aghu.faturamento.business;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.faturamento.dao.FatCaractItemProcHospDAO;
import br.gov.mec.aghu.faturamento.dao.FatTipoCaractItensDAO;
import br.gov.mec.aghu.model.FatCaractItemProcHosp;
import br.gov.mec.aghu.model.FatTipoCaractItens;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseRuntimeException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class FatTipoCaractItensRN  extends BaseBusiness {

	private static final long serialVersionUID = -5463992318547398321L;
	
	private static final Log LOG = LogFactory.getLog(FatTipoCaractItensRN.class);
	
	@Inject
	private FatTipoCaractItensDAO fatTipoCaractItensDAO;
	
	@Inject
	private FatCaractItemProcHospDAO fatCaractItemProcHospDAO;

	@Override
	protected Log getLogger() {
		return LOG;
	}
	
	private enum FatTipoCaractItensRNException implements BusinessExceptionCode {
		ITEM_NAO_EXISTE_FTCI;
	}
	

	//I1
	public void inserirFatTipoCaractItens(FatTipoCaractItens entity) {
		fatTipoCaractItensDAO.persistir(entity);
	}
	
	//U1
	public void atualizarFatTipoCaractItens(FatTipoCaractItens entity) throws ApplicationBusinessException {
		FatTipoCaractItens original = fatTipoCaractItensDAO.obterPorChavePrimaria(entity.getSeq());
		if(original == null) {
			throw new ApplicationBusinessException(FatTipoCaractItensRNException.ITEM_NAO_EXISTE_FTCI);
		}
		fatTipoCaractItensDAO.merge(entity);
	}
	
	//D1
	public void excluirFatTipoCaractItensPorSeq(Integer seq) throws ApplicationBusinessException {
		try {
			FatTipoCaractItens entity = fatTipoCaractItensDAO.obterPorChavePrimaria(seq);
			if(entity != null) {
				excluirCaracteristicasItemProcHosp(entity);
				fatTipoCaractItensDAO.remover(entity);
			}
		} catch (BaseRuntimeException e) {
		 throw new ApplicationBusinessException(FatTipoCaractItensRNException.ITEM_NAO_EXISTE_FTCI, e);
		}
	}
	
	// Delete em cascata para tabela FAT_CARACT_ITEM_PROC_HOSP, conforme esclarecido por email com analista.
	//D1 - método auxiliar
	private void excluirCaracteristicasItemProcHosp(FatTipoCaractItens entity) {
		for (FatCaractItemProcHosp item : entity.getCaracteristicasItemProcHosp()) {
			fatCaractItemProcHospDAO.removerPorId(item.getId());	
		}
	}

}
