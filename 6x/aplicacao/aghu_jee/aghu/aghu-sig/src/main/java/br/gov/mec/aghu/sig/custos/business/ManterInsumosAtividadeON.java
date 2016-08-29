package br.gov.mec.aghu.sig.custos.business;

import java.math.BigDecimal;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.estoque.business.IEstoqueFacade;
import br.gov.mec.aghu.model.SceEstoqueGeral;
import br.gov.mec.aghu.model.ScoMaterial;
import br.gov.mec.aghu.model.SigAtividadeInsumos;
import br.gov.mec.aghu.sig.dao.SigAtividadeInsumosDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterInsumosAtividadeON extends BaseBusiness{

private static final Log LOG = LogFactory.getLog(ManterInsumosAtividadeON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private SigAtividadeInsumosDAO sigAtividadeInsumosDAO;

@EJB
private IEstoqueFacade estoqueFacade;

	private static final long serialVersionUID = -4875196762395332941L;

	public enum ManterInsumosAtividadeONExceptionCode implements BusinessExceptionCode {
		MSG_INSUMO_ATIV_INVALIDO,
		MSG_UNIDADE_MEDIDA_OBRIG,
		MSG_QTDE_UTILIZADA_OBRIG,
		MSG_UNIDADE_TEMPO_OBRIG,
		MSG_TEMPO_OBRIG,
		MSG_UTILIZACAO_INSUMO_EXCLUSIVA,
		MSG_INSUMO_EXISTENTE,
		MSG_QTDE_PROD_OU_TEMPO;
	}
	
	public void validarInclusaoInsumoAtividade(SigAtividadeInsumos atividadeInsumos, List<SigAtividadeInsumos> listAtividadeInsumos) throws ApplicationBusinessException {
		
		if(atividadeInsumos == null){
			throw new ApplicationBusinessException(
					ManterInsumosAtividadeONExceptionCode.MSG_INSUMO_ATIV_INVALIDO);
		}
		
		validaInsumoExistente(atividadeInsumos, listAtividadeInsumos);
		validarUtilizacaoInsumoExclusiva(atividadeInsumos);
		validarQtdeEspecifica(atividadeInsumos);
		validarQtdeVidaUtil(atividadeInsumos);
	}
	
	public void validarAlteracaoInsumoAtividade(SigAtividadeInsumos atividadeInsumos, List<SigAtividadeInsumos> listAtividadeInsumos) throws ApplicationBusinessException {
		
		if(atividadeInsumos == null){
			throw new ApplicationBusinessException(
					ManterInsumosAtividadeONExceptionCode.MSG_INSUMO_ATIV_INVALIDO);
		}
		
		validarUtilizacaoInsumoExclusiva(atividadeInsumos);
		validarQtdeEspecifica(atividadeInsumos);
		validarQtdeVidaUtil(atividadeInsumos);
	}
	
	private void validaInsumoExistente(SigAtividadeInsumos atividadeInsumos,
			List<SigAtividadeInsumos> listAtividadeInsumos)  throws ApplicationBusinessException {
		
		for(SigAtividadeInsumos ativInsumo : listAtividadeInsumos){
			if(ativInsumo.getMaterial().getCodigo().equals(atividadeInsumos.getMaterial().getCodigo())){
				throw new ApplicationBusinessException(
						ManterInsumosAtividadeONExceptionCode.MSG_INSUMO_EXISTENTE);
			}
		}
	}

	public void validarUtilizacaoInsumoExclusiva(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException {
		
		if((atividadeInsumos.getQtdeUso() != null || atividadeInsumos.getUnidadeMedida() != null) &&
		   (atividadeInsumos.getVidaUtilQtde() != null ||
			atividadeInsumos.getVidaUtilTempo() != null || atividadeInsumos.getDirecionadores() != null)){
			throw new ApplicationBusinessException(
					ManterInsumosAtividadeONExceptionCode.MSG_UTILIZACAO_INSUMO_EXCLUSIVA);
		}
	}

	public void validarQtdeEspecifica(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException {
		
		if(atividadeInsumos.getQtdeUso() != null && atividadeInsumos.getUnidadeMedida() == null){
			throw new ApplicationBusinessException(
					ManterInsumosAtividadeONExceptionCode.MSG_UNIDADE_MEDIDA_OBRIG);
		}else{
			if(atividadeInsumos.getUnidadeMedida() != null && atividadeInsumos.getQtdeUso() == null){
				throw new ApplicationBusinessException(
						ManterInsumosAtividadeONExceptionCode.MSG_QTDE_UTILIZADA_OBRIG);
			}
		}
	}
	
	public void validarQtdeVidaUtil(SigAtividadeInsumos atividadeInsumos) throws ApplicationBusinessException {
		
		if(atividadeInsumos.getVidaUtilQtde() != null &&
		   atividadeInsumos.getVidaUtilTempo() != null && 
		   atividadeInsumos.getDirecionadores() != null){
			throw new ApplicationBusinessException(
					ManterInsumosAtividadeONExceptionCode.MSG_QTDE_PROD_OU_TEMPO);
		}
		
		if(atividadeInsumos.getVidaUtilTempo() != null && atividadeInsumos.getDirecionadores() == null){
			throw new ApplicationBusinessException(
					ManterInsumosAtividadeONExceptionCode.MSG_UNIDADE_TEMPO_OBRIG);
		}else{
			if(atividadeInsumos.getDirecionadores() != null && atividadeInsumos.getVidaUtilTempo() == null){
				throw new ApplicationBusinessException(
						ManterInsumosAtividadeONExceptionCode.MSG_TEMPO_OBRIG);
			}
		}
	}
	
	public void persistirInsumos(SigAtividadeInsumos sigAtividadeInsumos){
		//alteração
		if(sigAtividadeInsumos.getSeq() != null){
			this.getSigAtividadeInsumosDAO().atualizar(sigAtividadeInsumos);
		//inclusão	
		}else {
			this.getSigAtividadeInsumosDAO().persistir(sigAtividadeInsumos);
		}
	}
	
	public BigDecimal efetuarCalculoCustoMedioMaterial(ScoMaterial material) {
		BigDecimal custoMedio = null;
		if (material != null && material.getCodigo() != null) {
			Date dtCompetencia = null;
			List<SceEstoqueGeral> lista = this.getEstoqueFacade().listarEstoqueMaterial(material.getCodigo());
			for (SceEstoqueGeral estqGeral : lista) {
				if (dtCompetencia == null) {
					if (estqGeral.getFornecedor().getNumero() == 1) {
						dtCompetencia = estqGeral.getId().getDtCompetencia();
						custoMedio = estqGeral.getCustoMedioPonderado();
					}
				} else {
					if (estqGeral.getId().getDtCompetencia().after(dtCompetencia)) {
						if (estqGeral.getFornecedor().getNumero() == 1) {
							dtCompetencia = estqGeral.getId().getDtCompetencia();
							custoMedio = estqGeral.getCustoMedioPonderado();
						}
					}
				}
			}
		}
		return custoMedio;
	}

	
	protected IEstoqueFacade getEstoqueFacade(){
		return this.estoqueFacade;
	}
	
	// DAOs
	protected SigAtividadeInsumosDAO getSigAtividadeInsumosDAO() {
		return sigAtividadeInsumosDAO;
	}

}
