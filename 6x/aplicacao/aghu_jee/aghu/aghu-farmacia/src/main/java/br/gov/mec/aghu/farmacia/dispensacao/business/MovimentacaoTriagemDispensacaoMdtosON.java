package br.gov.mec.aghu.farmacia.dispensacao.business;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoItemPrescritoDispensacaoMdto;
import br.gov.mec.aghu.farmacia.business.IFarmaciaFacade;
import br.gov.mec.aghu.farmacia.dao.AfaDispensacaoMdtosDAO;
import br.gov.mec.aghu.model.AfaDispensacaoMdtos;
import br.gov.mec.aghu.model.AfaMedicamento;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;

@Stateless
public class MovimentacaoTriagemDispensacaoMdtosON extends BaseBusiness
		implements Serializable {


@EJB
private MovimentacaoTriagemDispensacaoMdtosRN movimentacaoTriagemDispensacaoMdtosRN;

private static final Log LOG = LogFactory.getLog(MovimentacaoTriagemDispensacaoMdtosON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@EJB
private IPrescricaoMedicaFacade prescricaoMedicaFacade;

@Inject
private AfaDispensacaoMdtosDAO afaDispensacaoMdtosDAO;

@EJB
private IFarmaciaDispensacaoFacade farmaciaDispensacaoFacade;

@EJB
private IFarmaciaFacade farmaciaFacade;

	public enum MovimentacaoTriagemDispensacaoMdtosONExceptionCode implements BusinessExceptionCode {
		CODIGO_MEDICAMENTO_NAO_EXISTE, MEDICAMENTO_NAO_EQUIVALENTE, CAMPO_OBRIGATORIO_MOV_TRIAGEM
	}
	/**
	 * 
	 */
	private static final long serialVersionUID = -4576109585765073831L;
	// TODO CONSTANTES, VERIFICAR SE POE NO AGHPARAMETROS
	private final static int QTDE_MDTOS_LISTA_MOVIMENTO_TRIAGEM = 5;

	public List<MpmItemPrescricaoMdto> recuperarListaTriagemMedicamentosPrescritos(
			Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc, MpmPrescricaoMedica prescricaoMedica, AfaMedicamento medicamento) {

		List<MpmItemPrescricaoMdto> ListaMedicamentos = getPrescricaoMedicaFacade().
			pesquisarItensPrescricaoMdtosPeloSeqAtdSeqDataIndPendente(
						firstResult, maxResult, orderProperty, asc,
						prescricaoMedica, medicamento);

		for (MpmItemPrescricaoMdto prescMdto : ListaMedicamentos) {
			prescMdto
					.setDescricaoMedicamentoPrescrito(getMovimentacaoTriagemDispensacaoMdtosRN()
							.obterDescricaoMedicamentoPrescrito(
									prescricaoMedica, prescMdto));
		}

		return ListaMedicamentos;
	}

	public List<AfaDispensacaoMdtos> pesquisarDispensacaoMdtoPorItemPrescrito(
			MpmItemPrescricaoMdto itemPrescrito,
			MpmPrescricaoMedica prescricaoMedica, boolean objectAtachado) {

		List<AfaDispensacaoMdtos> medicamentosDispensacao = getAfaDispensacaoMdtosDAO()
				.pesquisarDispensacaoMdtoPorItemPrescrito(itemPrescrito,
						prescricaoMedica);
		
		//Desatachando lista
		for(AfaDispensacaoMdtos dispMdto:medicamentosDispensacao){
			//Acessa atributos atráves de lazy antes de desatachar
			dispMdto.getMedicamento().getDescricaoEditada();
			dispMdto.getServidor();
			dispMdto.getServidorConferida();
			dispMdto.getServidorEntregue();
			dispMdto.getServidorEstornado();
			dispMdto.getServidorTriadoPor();
			dispMdto.getItemPrescricaoMdto();
			dispMdto.getPrescricaoMedica();
			if(dispMdto.getMedicamento() != null) {
				dispMdto.setMatCodigoMdtoSelecionado(dispMdto.getMedicamento().getMatCodigo().toString());
			}
			if(dispMdto.getUnidadeFuncional()!=null) {
				dispMdto.setSeqUnidadeFuncionalSelecionada(dispMdto.getUnidadeFuncional().getSeq().toString());
			}
			if(dispMdto.getTipoOcorrenciaDispensacao()!=null) {
				dispMdto.setSeqAfaTipoOcorSelecionada(dispMdto.getTipoOcorrenciaDispensacao().getSeq().toString());
			}
			dispMdto.setIndSituacaoNova(dispMdto.getIndSituacao());
			getFarmaciaDispensacaoFacade().processaImagensSituacoesDispensacao(dispMdto);
			if(!objectAtachado) {
				getAfaDispensacaoMdtosDAO().desatachar(dispMdto);
			}	
		}
		return medicamentosDispensacao;
	}

	public String recuperaDescricaoMdtoPrescrito(
			MpmPrescricaoMedica prescricao, MpmItemPrescricaoMdto itemPrescrito) {

		return obtemDescricaoMedicamentoPrescrito(itemPrescrito,prescricao);
	}

	public DominioSituacaoItemPrescritoDispensacaoMdto verificarSituacaoItemPrescritoIncluidoFarmacia(
			AfaDispensacaoMdtos selecao) {

		if (selecao.getMedicamento() != null
				|| selecao.getIndSituacao() != null
				|| (selecao.getQtdeDispensada() != null && selecao
						.getQtdeDispensada().intValue() != 0)
				|| selecao.getTipoOcorrenciaDispensacao() != null
				|| selecao.getUnidadeFuncional() != null) {
			if (selecao.getIndSitItemPrescrito() == null) {
				return DominioSituacaoItemPrescritoDispensacaoMdto.IF;
			}
		}
		return selecao.getIndSitItemPrescrito();
	}

	private String obtemDescricaoMedicamentoPrescrito(
			MpmItemPrescricaoMdto itemPrescrito, MpmPrescricaoMedica prescricaoMedica) {

		String descricaoMdtoPrescrito = null;
		descricaoMdtoPrescrito = getMovimentacaoTriagemDispensacaoMdtosRN()
			.obterDescricaoMedicamentoPrescrito(prescricaoMedica, itemPrescrito);
		return descricaoMdtoPrescrito;
	}

	public void persistirMovimentoDispensacaoMdtos(
			MpmPrescricaoMedica prescricaoMedica,
			MpmItemPrescricaoMdto itemPrescrito,
			List<AfaDispensacaoMdtos> medicamentosDispensadosModificados,
			List<AfaDispensacaoMdtos> medicamentosDispensadosOriginal,
			String nomeMicrocomputador)
			throws BaseException {

		validaCamposObrigatoriosLista(medicamentosDispensadosModificados);
		getMovimentacaoTriagemDispensacaoMdtosRN()
				.persistirMovimentosDispensacaoMdtos(prescricaoMedica,
						itemPrescrito, medicamentosDispensadosModificados
						,medicamentosDispensadosOriginal, nomeMicrocomputador);
	}
	
	private void validaCamposObrigatoriosLista(
			List<AfaDispensacaoMdtos> medicamentosDispensadosModificados) throws ApplicationBusinessException {
		for(AfaDispensacaoMdtos mdto : medicamentosDispensadosModificados){
			if(mdto.getMedicamento() == null || mdto.getMedicamento().getMatCodigo() == null){
				throw new ApplicationBusinessException(MovimentacaoTriagemDispensacaoMdtosONExceptionCode.CAMPO_OBRIGATORIO_MOV_TRIAGEM, "Medicamento");
			}
		}
		
	}

	// Util
	
	public List<AfaDispensacaoMdtos> criarListaNovaMovimentacao() {
		
		List<AfaDispensacaoMdtos> medicamentosDispensacao = new ArrayList<AfaDispensacaoMdtos>();
		for (int i = 0; i < QTDE_MDTOS_LISTA_MOVIMENTO_TRIAGEM; i++) {
			medicamentosDispensacao.add(new AfaDispensacaoMdtos());
		}
		return medicamentosDispensacao;
	}

	private AfaDispensacaoMdtosDAO getAfaDispensacaoMdtosDAO() {
		return afaDispensacaoMdtosDAO;
	}

	private IPrescricaoMedicaFacade getPrescricaoMedicaFacade(){
		return prescricaoMedicaFacade;
	}
	private MovimentacaoTriagemDispensacaoMdtosRN getMovimentacaoTriagemDispensacaoMdtosRN() {
		return movimentacaoTriagemDispensacaoMdtosRN;
	}

	private IFarmaciaFacade getFarmaciaFacade() {
		return this.farmaciaFacade;
	}
	
	public void processaMdtoSelecaoInMovTriagemDisp(AfaDispensacaoMdtos adm,
			List<AfaMedicamento> medicamentos) throws ApplicationBusinessException {
		AfaMedicamento medi = adm.getMatCodigoMdtoSelecionado()!=null && !adm.getMatCodigoMdtoSelecionado().trim().equals("")?getFarmaciaFacade().obterMedicamento(Integer.valueOf(adm.getMatCodigoMdtoSelecionado())):null;
		if (medi != null){
			if(medicamentos.contains(medi)){
				adm.setMedicamento(medi);
			}else{
				throw new ApplicationBusinessException(MovimentacaoTriagemDispensacaoMdtosONExceptionCode.MEDICAMENTO_NAO_EQUIVALENTE);
			}
		}else {
			throw new ApplicationBusinessException(MovimentacaoTriagemDispensacaoMdtosONExceptionCode.CODIGO_MEDICAMENTO_NAO_EXISTE);
		}
			
		
	}
	
	protected IFarmaciaDispensacaoFacade getFarmaciaDispensacaoFacade() {
		return this.farmaciaDispensacaoFacade;
	}

}
