package br.gov.mec.aghu.estoque.business;

import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.AbstractAGHUCrudRn;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.SceLoteDocImpressao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@SuppressWarnings({"PMD.HierarquiaONRNIncorreta"})
@Stateless
public class ImprimirEtiquetasExtrasRN extends AbstractAGHUCrudRn<SceLoteDocImpressao>{
	
	private static final long serialVersionUID = -8388001627175893853L;
	
	private static final Log LOG = LogFactory.getLog(ImprimirEtiquetasExtrasRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;

	@EJB
	private IEstoqueFacade estoqueFacade;

	@EJB
	private IFarmaciaFacade farmaciaFacade;

	public enum ImprimirEtiquetasExtrasRNExceptionCode implements BusinessExceptionCode {
		AFA_00169;
	}

	@Override
	public boolean bsiPreInsercaoStatement(SceLoteDocImpressao entidade,
			String nomeMicrocomputador,
			final Date dataFimVinculoServidor) 
				throws BaseException {
		
		setNroInicialNroFinal(entidade);
		setConcentracaoDescricao(entidade);
//		setLote(entidade);
		return super.bsiPreInsercaoStatement(entidade, nomeMicrocomputador, dataFimVinculoServidor);		
		
	}

		
	/**
	 * @ORADB AGH.SCET_LDI_BRI
	 */
	@Override
	public boolean briPreInsercaoRow(SceLoteDocImpressao entidade, String nomeMicrocomputador, final Date dataFimVinculoServidor)
			throws BaseException {
		
		setSolicitadoEm(entidade);
		
		//if(entidade.getSeq() == null){
			//Id é atribuído automaticamente, passo escrito somente para deixar transcrição da Trigger explicita
			//:new.seq := scec_get_sce_ldi_sq1_nextval;
		//}
		
		// implementacao do codigo abaixo ja consta no metodo bsiPreInsercaoStatement-setNroInicialNroFinal (pre-insert do Oracle Forms) 
		
		// if lenght(:new.nro_inicial) > 5 or lenght(:new.nro_final) > 5 then
		// :new.nro_inicial := 1;
		// :new.nro_final := :new.nro_inicial + :new.qtde;
		// end if;
		
		setServidor(entidade);		
		
		return super.briPreInsercaoRow(entidade, nomeMicrocomputador, dataFimVinculoServidor);
		
	}
	
	
	protected void setSolicitadoEm(SceLoteDocImpressao entidade) {
		
		entidade.setSolicitadoEm(getDataCriacao());
		
	}

	
	protected void setServidor(SceLoteDocImpressao entidade) throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();
		
		entidade.setServidor(servidorLogado);
		
		if (entidade.getServidor() == null){
			throw new ApplicationBusinessException(ImprimirEtiquetasExtrasRNExceptionCode.AFA_00169);
		}
		
	}
	
	
	protected void setNroInicialNroFinal(SceLoteDocImpressao entidade) {

		List<SceLoteDocImpressao> list = getEstoqueFacade().pesquisarPorDadosInformados(entidade);
		
		SceLoteDocImpressao ent = (list!=null && !list.isEmpty()) ? list.get(0) : null;

		if(ent == null){
			entidade.setNroInicial(1);
			entidade.setNroFinal(entidade.getQtde());			
		}else{
			if(ent.getNroFinal() != null){
				entidade.setNroInicial(ent.getNroFinal() + 1);
				entidade.setNroFinal( (entidade.getNroInicial() + entidade.getQtde()) - 1 );

				Integer numeroIni = entidade.getNroInicial().toString().length(); 
				Integer numeroFim = entidade.getNroFinal().toString().length(); 

				if (numeroIni > 5 || numeroFim > 5){
					entidade.setNroInicial(1);
					entidade.setNroFinal( (entidade.getNroInicial() + entidade.getQtde()) - 1 );				
				}			
			}	
		}

	}
	
	
	protected void setConcentracaoDescricao(SceLoteDocImpressao entidade) {
		AfaMedicamento medicamento = getFarmaciaFacade().obterMedicamentoPorMatCodigo(entidade.getMaterial().getCodigo());
		if(medicamento.getConcentracao() != null) {
			
			entidade.setConcentracao(medicamento.getConcentracao());
		} else {
			entidade.setConcentracao(null);
		}
		
		if(medicamento.getMpmUnidadeMedidaMedicas() != null
				&& medicamento.getMpmUnidadeMedidaMedicas().getDescricao() != null) {
			
			entidade.setUmmDescricao(medicamento.getMpmUnidadeMedidaMedicas().getDescricao());	
		} else {
			entidade.setUmmDescricao(null);
		}
	}

	
//	private void setLote(SceLoteDocImpressao entidade) {
//		
//		SceLoteId sceLoteId = new SceLoteId();
//		sceLoteId.setCodigo(entidade.getLote().getId().getCodigo());
//		sceLoteId.setMatCodigo(entidade.getMaterial().getCodigo());
//		sceLoteId.setMcmCodigo(entidade.getMarcaComercial().getCodigo());
//		
//		SceLote sceLote = new SceLote();
//		sceLote.setId(sceLoteId);
//		
//		entidade.setLote(sceLote);
//		
//	}

	protected IEstoqueFacade getEstoqueFacade() {
		return this.estoqueFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}

	protected IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
}