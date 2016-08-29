package br.gov.mec.aghu.prescricaomedica.business;

import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.AelUnfExecutaExamesId;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExame;
import br.gov.mec.aghu.model.MpmAltaItemPedidoExameId;
import br.gov.mec.aghu.model.MpmAltaSumarioId;
import br.gov.mec.aghu.prescricaomedica.dao.MpmAltaItemPedidoExameDAO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class ManterAltaItemPedidoExameRN extends BaseBusiness {


@EJB
private ManterAltaSumarioRN manterAltaSumarioRN;

private static final Log LOG = LogFactory.getLog(ManterAltaItemPedidoExameRN.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmAltaItemPedidoExameDAO mpmAltaItemPedidoExameDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5447440298217988963L;

	public enum ManterAltaItemPedidoExameRNExceptionCode implements
			BusinessExceptionCode {

		ERRO_INSERIR_ITEM_PEDIDO_EXAME,
		ERRO_INSERIR_ITEM_PEDIDO_EXAME_DUPLICADO;

		public void throwException(Throwable cause, Object... params)
				throws ApplicationBusinessException {
			// Tratamento adicional para não esconder a excecao de negocio
			// original
			if (cause instanceof ApplicationBusinessException) {
				throw (ApplicationBusinessException) cause;
			}
			throw new ApplicationBusinessException(this, cause, params);
		}

	}

	/**
	 * Insere objeto MpmAltaItemPedidoExame.
	 * 
	 * @param {MpmAltaItemPedidoExame} altaItemPedidoExame
	 */
	public void inserirAltaItemPedidoExame(
			MpmAltaItemPedidoExame altaItemPedidoExame)
			throws ApplicationBusinessException {
		
		
		List<MpmAltaItemPedidoExame> itens = this.getAltaItemPedidoExameDAO().obterMpmAltaItemPedidoExame(altaItemPedidoExame.getMpmAltaSumarios().getId().getApaAtdSeq(), altaItemPedidoExame.getMpmAltaSumarios().getId().getApaSeq(), altaItemPedidoExame.getMpmAltaSumarios().getId().getSeqp());
		MpmAltaSumarioId altaSumarioId = altaItemPedidoExame.getMpmAltaSumarios().getId();
		AelUnfExecutaExamesId executaExamesId = altaItemPedidoExame.getAelUnfExecutaExames().getId();
		MpmAltaItemPedidoExameId altaItemPedidoExameId = new MpmAltaItemPedidoExameId();
		altaItemPedidoExameId.setAexAsuApaAtdSeq(altaSumarioId.getApaAtdSeq());
		altaItemPedidoExameId.setAexAsuApaSeq(altaSumarioId.getApaSeq());
		altaItemPedidoExameId.setAexAsuSeqp(altaSumarioId.getSeqp());
		altaItemPedidoExameId.setUfeEmaExaSigla(executaExamesId.getEmaExaSigla());
		altaItemPedidoExameId.setUfeEmaManSeq(executaExamesId.getEmaManSeq());
		altaItemPedidoExameId.setUfeUnfSeq(executaExamesId.getUnfSeq());
		
		altaItemPedidoExame.setId(altaItemPedidoExameId);
		
		for(MpmAltaItemPedidoExame test : itens){
			if(test.getId().equals(altaItemPedidoExame.getId())){
				throw new ApplicationBusinessException(
						ManterAltaItemPedidoExameRNExceptionCode.ERRO_INSERIR_ITEM_PEDIDO_EXAME_DUPLICADO);
			}
		}
		altaItemPedidoExame.setId(null);

		try {

			this.preInserirAltaItemPedidoExame(altaItemPedidoExame);
			this.getAltaItemPedidoExameDAO().persistir(altaItemPedidoExame);
			this.getAltaItemPedidoExameDAO().flush();

		} catch (Exception e) {

			ManterAltaItemPedidoExameRNExceptionCode.ERRO_INSERIR_ITEM_PEDIDO_EXAME
					.throwException(e);

		}

	}

	/**
	 * ORADB Trigger MPMT_AIP_BRI
	 * 
	 * @param {MpmAltaItemPedidoExame} altaItemPedidoExame
	 */
	protected void preInserirAltaItemPedidoExame(
			MpmAltaItemPedidoExame altaItemPedidoExame)
			throws ApplicationBusinessException {

		// Verifica se ALTA_SUMARIOS está ativo
		getAltaSumarioRN().verificarAltaSumarioAtivo(
				altaItemPedidoExame.getMpmAltaSumarios());

	}
	
	/**
	 * Remover objeto MpmAltaItemPedidoExame.
	 * 
	 * @param {MpmAltaItemPedidoExame} altaItemPedidoExame
	 * @throws ApplicationBusinessException
	 */
	public void removerAltaItemPedidoExame(MpmAltaItemPedidoExame altaItemPedidoExame) throws ApplicationBusinessException {
		altaItemPedidoExame.getMpmAltaSumarios().setAltaEstadoPaciente(null);
		altaItemPedidoExame.setMpmAltaSumarios(null);
		this.getAltaItemPedidoExameDAO().remover(altaItemPedidoExame);
		this.getAltaItemPedidoExameDAO().flush();

	}

	protected ManterAltaSumarioRN getAltaSumarioRN() {
		return manterAltaSumarioRN;
	}

	protected MpmAltaItemPedidoExameDAO getAltaItemPedidoExameDAO() {
		return mpmAltaItemPedidoExameDAO;
	}
}
