package br.gov.mec.aghu.prescricaomedica.action;

import java.net.UnknownHostException;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.cups.business.ICupsFacade;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmListaServSumrAlta;
import br.gov.mec.aghu.paciente.prontuarioonline.action.RelatorioHistoricoPacienteController;
import br.gov.mec.aghu.prescricaomedica.action.RelatorioSumarioAltaController.Subtitulo;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class ListarSumarioAltaReimpressaoController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = 5930873180016510077L;

	private static final Log LOG = LogFactory.getLog(ListarSumarioAltaReimpressaoController.class);
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private ICupsFacade cupsFacade;

	@Inject
	@SelectionQualifier
	private RelatorioSumarioAltaController relatorioSumarioAltaController;
	
	@Inject
	private RelatorioHistoricoPacienteController relatorioHistoricoPacienteController;

	private Integer prontuario = null;

	private Integer codigo = null;

	private Integer atdSeq;
	
	private boolean exibirModal = false;
	
	private MpmListaServSumrAlta selecionado;

	@Inject @Paginator
	private DynamicDataModel<MpmListaServSumrAlta> dataModel;
	
	private static final String RELATORIO_SUMARIO_ALTA = "prescricaomedica-relatorioSumarioAlta";
	private static final String LISTAR_SUMARIO_ALTA_REIMPRESSAO = "prescricaomedica-listarSumarioAltaReimpressao";

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}

	public void carregarModal() {
		this.exibirModal = true;
	}
	
	public void refazer() {
		try {
			String nomeMicrocomputador = null;
			try {
				nomeMicrocomputador = super.getEnderecoRedeHostRemoto();
			} catch (UnknownHostException e) {
				LOG.error("Exceção caputada:", e);
			}
			this.prescricaoMedicaFacade.refazer(atdSeq, nomeMicrocomputador, new Date());
			this.dataModel.reiniciarPaginator();
			this.exibirModal = false;
			apresentarMsgNegocio(Severity.INFO, "REFAZER_REALIZADO_SUCESSO");			
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}
	
	public void pesquisar() {
		if (prontuario != null || codigo != null) {
			dataModel.reiniciarPaginator();
		} else {
			apresentarMsgNegocio(Severity.ERROR, "PRONTUARIO_OU_CODIGO_OBRIGATORIO");
		}
	}

	public void limparPesquisa() {
		prontuario = null;
		codigo = null;
		selecionado = null;
		dataModel.limparPesquisa();
	}

	@Override
	public Long recuperarCount() {
		if (prontuario != null || codigo != null) {
			return prescricaoMedicaFacade.countPesquisaListaSumAltaReimp(prontuario, codigo);
		}
		return null;
	}

	@Override
	public List<MpmListaServSumrAlta> recuperarListaPaginada(Integer firstResult,Integer maxResults, String orderProperty, boolean asc) {
		return prescricaoMedicaFacade.listaPesquisaListaSumAltaReimp( firstResult, maxResults, 
																	  AghAtendimentos.Fields.DTHR_FIM.toString(), false,
																	  prontuario, codigo);
	}
	
	public Long obterQtAltasSumario(Integer seqAtendimento){
		return prescricaoMedicaFacade.obterQtAltasSumario(seqAtendimento);
	}

	public String reimprimir(Integer seqAtendimento){
		relatorioSumarioAltaController.setSeqAtendimento(seqAtendimento);
		relatorioSumarioAltaController.setPrevia(false);
		relatorioSumarioAltaController.setSubtitulo(Subtitulo.REIMPRESSAO.toString());
		relatorioSumarioAltaController.setVoltarPara(LISTAR_SUMARIO_ALTA_REIMPRESSAO);
        relatorioSumarioAltaController.populaRelatorioAltaObito();
		
		return RELATORIO_SUMARIO_ALTA;
	}
	
	public void imprimirHistoricoPaciente(Integer prontuario) {
		try {
			cupsFacade.verificarCupsAtivo();
			relatorioHistoricoPacienteController.observarGeracaoRelatorioHistoricoPaciente(prontuario);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public Integer getCodigo() {
		return codigo;
	}

	public void setCodigo(Integer codigo) {
		this.codigo = codigo;
	}
	public DynamicDataModel<MpmListaServSumrAlta> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<MpmListaServSumrAlta> dataModel) {
	 this.dataModel = dataModel;
	}

	public MpmListaServSumrAlta getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(MpmListaServSumrAlta selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isExibirModal() {
		return exibirModal;
	}

	public void setExibirModal(boolean exibirModal) {
		this.exibirModal = exibirModal;
	}

	public Integer getAtdSeq() {
		return atdSeq;
	}

	public void setAtdSeq(Integer atdSeq) {
		this.atdSeq = atdSeq;
	}
}