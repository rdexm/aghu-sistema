package br.gov.mec.aghu.paciente.prontuarioonline.action;

import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.context.RequestScoped;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.beanutils.BeanComparator;
import org.apache.commons.collections.comparators.ComparatorChain;
import org.apache.commons.collections.comparators.NullComparator;
import org.apache.commons.collections.comparators.ReverseComparator;

import br.gov.mec.aghu.ambulatorio.action.ConsultarAmbulatorioPOLRelatorioController;
import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.ambulatorio.vo.DocumentosPacienteVO;
import br.gov.mec.aghu.ambulatorio.vo.RelatorioAnamneseEvolucaoVO;
import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.certificacaodigital.action.VisualizarDocumentoController;
import br.gov.mec.aghu.controlepaciente.business.IControlePacienteFacade;
import br.gov.mec.aghu.dominio.DominioAnamneseEvolucao;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.EcpHorarioControle;
import br.gov.mec.aghu.model.MbcFichaAnestesias;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.prontuarioonline.business.IProntuarioOnlineFacade;
import br.gov.mec.aghu.paciente.prontuarioonline.vo.NodoPOLVO;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.action.SecurityController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.exception.Severity;
import br.gov.mec.aghu.core.utils.DateUtil;

public class ConsultarAmbulatorioPOLController extends ActionController implements ActionPaginator {

	private static final long serialVersionUID = -4934814947870039276L;
	
	private static final String RELATORIO_AMBULATORIO_PDF = "pol-relatorioAmbulatorio";
	private static final String PACIENTE_CONSULTA_AMBULATORIO = "paciente-consultaAmbulatorio";
	private static final String DOCUMENTO_ASSINADO_CONSUL_AMBULATORIO_POL = "certificacaodigital-visualizarDocumentoAssinadoPOL";
	private static final String RELATORIO_ATOS_ANESTESICOS_CONSULTA_AMBULATORIO = "relatorioAtosAnestesicosPdf";
	private static final String VISUALIZAR_RELATORIO_ALTA_AMBULATORIAL_AMBULATORIO = "altaAmbulatorialPolPdf";
	private static final String POL_AMBULATORIO = "pol-ambulatorio";
	private static final String RELATORIO_CONTROLES_PACIENTE = "paciente-relatorioControlesPaciente";

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	@EJB
	private IAmbulatorioFacade ambulatorioFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IProntuarioOnlineFacade prontuarioOnlineFacade;
	
	@EJB
	private IControlePacienteFacade controlePacienteFacade;
	

	@Inject
	private SecurityController securityController;	
	
	@Inject
	private ConsultarAmbulatorioPOLRelatorioController consultarAmbulatorioPOLRelatorioController;
	
	@Inject
	private VisualizarDocumentoController visualizarDocumentoController;

	@Inject
	private RelatorioAtosAnestesicosController relatorioAtosAnestesicosController;
	
	@Inject
	private ImprimeAltaAmbulatorialPolController imprimeAltaAmbulatorialPolController;

	@Inject @SelectionQualifier @RequestScoped
	private NodoPOLVO itemPOL;

	@Inject @Paginator
	private DynamicDataModel<AghAtendimentos> dataModel;
	
	@Inject
	private RelatorioRegistrosControlesPacienteController relatorioRegistrosControlesPacienteController;
	
	private AghAtendimentos selecionado;

	private String atendimentos;
	private String descricaoConsulta;
	private String nomeEspecialidade;
	private Date dtInicial;
	private Date dtFinal;
	private Boolean readOnly;
	private Boolean readOnlyDataTable;
	
	private MbcFichaAnestesias mbcFichaAnestesia;
	private Integer seqVersaoDocAssinado;
	
	private Map<AghAtendimentos, Boolean> atendimentosSelecionados = new HashMap<AghAtendimentos, Boolean>();
	
    private List<AghAtendimentos> allChecked;
	
	private Boolean temPermissaoAcessoAdminPOL;
	
	private String voltarPara;
	
	private boolean keepDescricao;
	
	
	public enum ConsultarAmbulatorioPOLControllerExceptionCode implements BusinessExceptionCode {
		DATA_INICIO_E_DATA_FIM_DEVEM_SER_INFORMADAS, DATA_INICIO_MENOR_DATA_FIM, SELECIONAR_CONSULTA, CONSULTA_NAO_FINALIZADA;
	}
	
	private Boolean selecionarTodos;
	
	private Long seqMamAltaSumario;

	private Integer prontuario;
	
	@PostConstruct
	protected void inicializar() {
		this.begin(conversation, true);
		temPermissaoAcessoAdminPOL = securityController.usuarioTemPermissao("acessoAdminPOL", "acessar");
		dtInicial = null;
		dtFinal = null;
		selecionado = null;
		keepDescricao = false;
	}
	
	public void inicio() {
		
		// Caso esteja voltando de alguma ação, não deve mudar o status da tela
		if(selecionado != null || (atendimentosSelecionados != null && !atendimentosSelecionados.isEmpty())){
			keepDescricao = true;
			return;
		}

		if(prontuario == null) {
			atendimentos = (String) itemPOL.getParametros().get("Atendimentos");
			prontuario = itemPOL.getProntuario();
		}
		
		if (prontuario != null) {
			dataModel.reiniciarPaginator();
			dataModel.setDefaultMaxRow(7);
			atendimentosSelecionados = new HashMap<AghAtendimentos, Boolean>();
			readOnly = false;
			readOnlyDataTable = false;
		}

		selecionarTodos = Boolean.FALSE;
	}
	
	public Boolean getUsuarioAdministrativo(){
		if(temPermissaoAcessoAdminPOL){
			return Boolean.TRUE;
		}else{
			return Boolean.FALSE;
		}
	}

	@Override
	public Long recuperarCount() {
		return this.prontuarioOnlineFacade.pesquisarAtendimentoAmbPorPacCodigoCount((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE), this.atendimentos);
	}

	@Override
	public List<AghAtendimentos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {
		List<AghAtendimentos> atendimentosLista = prontuarioOnlineFacade.pesquisarAtendimentoAmbPorPacCodigo( firstResult, maxResult, orderProperty, asc, 
																											   (Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE), 
																											   atendimentos);
		if (atendimentosLista == null) {
			atendimentosLista = new ArrayList<AghAtendimentos>();
		}
		
		//ORDENAÇÃO
		if(!atendimentosLista.isEmpty()) {
			final ComparatorChain chainSorter = new ComparatorChain();
			final BeanComparator dtOrdSorter = new BeanComparator("dthrInicioTruncada", new ReverseComparator(new NullComparator(false)));
			final BeanComparator especialidadeMedicacaoSorter = new BeanComparator("especialidadeAtendimento.nomeEspecialidade", new NullComparator(false));
			final BeanComparator dtIniciooSorter = new BeanComparator("dthrInicio", new ReverseComparator(new NullComparator(false)));
			chainSorter.addComparator(dtOrdSorter);
			chainSorter.addComparator(especialidadeMedicacaoSorter);
			chainSorter.addComparator(dtIniciooSorter);
			Collections.sort(atendimentosLista, chainSorter);
			
			if(atendimentosLista.size() == 1) {
				selecionado = atendimentosLista.get(0);
			} else {
				
				if(!keepDescricao){
					descricaoConsulta = null;
					nomeEspecialidade = null;
				}
				
				keepDescricao = false;
			}
			
			for(AghAtendimentos atendimento : atendimentosLista) {
				if(!atendimentosSelecionados.containsKey(atendimento)) {
					atendimentosSelecionados.put(atendimento, false);
				}
			}
			
			this.verificarData();
		}
		
		return atendimentosLista;
	}
	
	public void selecionarTodosAtendimentos(){
		Boolean msgExibida = Boolean.FALSE;
		dtFinal = dtInicial = null;
		
		List<AghAtendimentos> listAtd = this.aghuFacade.pesquisarAtendimentoAmbPorPacCodigo( 0, 99999,  null, true, 
																							  (Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE), 
																							  atendimentos);
		readOnly = false;
		atendimentosSelecionados = new HashMap<AghAtendimentos, Boolean>();
		
		for(AghAtendimentos att : listAtd){
			atendimentosSelecionados.put(att, selecionarTodos);
			if(selecionarTodos){//Para desmarcar nÃ£o Ã© necessÃ¡rio re-validar
				try {
					ambulatorioFacade.validarSelecaoImpressaoConsultaAmbulatorio((att.getConsulta() != null)?att.getConsulta().getNumero():null, selecionarTodos);
					readOnly = true;
				} catch(BaseException e) {
					atendimentosSelecionados.put(att, Boolean.FALSE);
					if(!msgExibida){
						msgExibida = Boolean.TRUE;
						apresentarExcecaoNegocio(e);
					}
				}
			}
		}
	}	
	
	public String nomeProfissional(RapServidores servidor) {
		try {
				Object[] obj = this.prescricaoMedicaFacade.buscaConsProf(servidor);
				
				if(obj != null) {
					return (String)obj[1];
				}
				
			} catch (ApplicationBusinessException e) {
				apresentarExcecaoNegocio(e);
			}
		return null;
	}
	
	public void selecionarLinha(AghAtendimentos selected, Boolean marcado) {
		try {
			dtFinal = dtInicial = null;
			Boolean selecionar = true;
			ambulatorioFacade.validarSelecaoImpressaoConsultaAmbulatorio((selected.getConsulta() != null)?selected.getConsulta().getNumero():null, selecionar);
			
			for(Map.Entry<AghAtendimentos, Boolean> atd : atendimentosSelecionados.entrySet()) {
				if(atd.getValue()) {
					readOnly = true;
					return;
				}
			}
			readOnly = false;
		} catch(BaseException e) {
			if (atendimentosSelecionados.containsKey(selected)){
			   atendimentosSelecionados.remove(selected);	
			   atendimentosSelecionados.put(selected, false);
			}
			apresentarExcecaoNegocio(e);
		}				
	}
	
	public void obtemDescricaoConsulta() {
		try {
			descricaoConsulta = ambulatorioFacade.visualizaConsulta(selecionado.getConsulta().getNumero(), 
																	selecionado.getPaciente().getCodigo()).toString();
			
			nomeEspecialidade = selecionado.getEspecialidade().getNomeEspecialidade();
			return;
		} catch(BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		descricaoConsulta = null;
		nomeEspecialidade = null;
	}
	
	public void verificarData() {
		if(dtInicial != null || dtFinal != null) {
			readOnlyDataTable = true;
		}
		else {
			readOnlyDataTable = false;
		}
	}
	
	public String imprimirControlePaciente() {
		relatorioRegistrosControlesPacienteController.setAghAtendimentos(selecionado);
		relatorioRegistrosControlesPacienteController.setVoltarAmbulatorioPOL(true);
			
		return RELATORIO_CONTROLES_PACIENTE;
	}
			
	public String print() throws BaseException, JRException, SystemException, IOException {
		
		List<RelatorioAnamneseEvolucaoVO> colecaoSelecionados = processaDocumentosRelatorioSelecionados();

		if(!colecaoSelecionados.isEmpty()){
			consultarAmbulatorioPOLRelatorioController.setColecao(colecaoSelecionados);
		}
		
		if (voltarPara != null){
			consultarAmbulatorioPOLRelatorioController.setVoltarPara(voltarPara);
			
		} else {
			consultarAmbulatorioPOLRelatorioController.setVoltarPara(PACIENTE_CONSULTA_AMBULATORIO);
		}
		
		if(readOnly) {
			if(colecaoSelecionados.isEmpty()) {
				apresentarMsgNegocio(Severity.ERROR, ConsultarAmbulatorioPOLControllerExceptionCode.CONSULTA_NAO_FINALIZADA.toString());
				return null;
			}
			consultarAmbulatorioPOLRelatorioController.setColecao(colecaoSelecionados);
			
		} else {
			if(dtInicial != null && dtFinal != null) {
				if(DateUtil.validaDataTruncadaMaior(dtFinal, dtInicial)) {
					List<AghAtendimentos> atendimentos = this.aghuFacade.pesquisarAtendimentoAmbPorPacCodigoEntreDtInicioEDtFim((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE), dtInicial, dtFinal);
					consultarAmbulatorioPOLRelatorioController.setColecao(processaDocumentosRelatorioEntreDatas(atendimentos));
				
				} else {
					apresentarMsgNegocio(Severity.ERROR,ConsultarAmbulatorioPOLControllerExceptionCode.DATA_INICIO_MENOR_DATA_FIM.toString());	
					return null;					
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR, ConsultarAmbulatorioPOLControllerExceptionCode.DATA_INICIO_E_DATA_FIM_DEVEM_SER_INFORMADAS.toString());	
				return null;
			}
			
			if(colecaoSelecionados.isEmpty()){
				apresentarMsgNegocio(Severity.ERROR, ConsultarAmbulatorioPOLControllerExceptionCode.SELECIONAR_CONSULTA.toString());	
		    	return null;
			}

		}
		
		return RELATORIO_AMBULATORIO_PDF;		
	}
	
	public void directPrint() throws BaseException, JRException, SystemException, IOException {
		List<RelatorioAnamneseEvolucaoVO> colecaoSelecionados = processaDocumentosRelatorioSelecionados();
		if(!colecaoSelecionados.isEmpty()){
			consultarAmbulatorioPOLRelatorioController.setColecao(colecaoSelecionados);
		}

		if(readOnly) {
			consultarAmbulatorioPOLRelatorioController.setColecao(colecaoSelecionados);
		}
		else {
			if(dtInicial != null && dtFinal != null) {
				if(DateUtil.validaDataTruncadaMaior(dtFinal, dtInicial)) {
					List<AghAtendimentos> atendimentos = this.aghuFacade.pesquisarAtendimentoAmbPorPacCodigoEntreDtInicioEDtFim((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE), dtInicial, dtFinal);
					consultarAmbulatorioPOLRelatorioController.setColecao(processaDocumentosRelatorioEntreDatas(atendimentos));
				
				} else {
					apresentarMsgNegocio(Severity.ERROR, ConsultarAmbulatorioPOLControllerExceptionCode.DATA_INICIO_MENOR_DATA_FIM.toString());	
					return;					
				}
			}
			else {
				apresentarMsgNegocio(Severity.ERROR, ConsultarAmbulatorioPOLControllerExceptionCode.DATA_INICIO_E_DATA_FIM_DEVEM_SER_INFORMADAS.toString());	
				return;
			}
			
			if(colecaoSelecionados.isEmpty()){
				apresentarMsgNegocio(Severity.ERROR, ConsultarAmbulatorioPOLControllerExceptionCode.SELECIONAR_CONSULTA.toString());
		    	return;
			}

		}
		consultarAmbulatorioPOLRelatorioController.directPrint();
	}
	
	public List<RelatorioAnamneseEvolucaoVO> processaDocumentosRelatorioEntreDatas(List<AghAtendimentos> atds) {
		
		List<RelatorioAnamneseEvolucaoVO> listaDadosRelatorio = new ArrayList<RelatorioAnamneseEvolucaoVO>(0);
		for(AghAtendimentos atd : atds) {
			if(atd.getConsulta() != null) {
				List<DocumentosPacienteVO> documentos = ambulatorioFacade.obterListaDocumentosPacienteAnamneseEvolucao(atd.getConsulta().getNumero());
				for(DocumentosPacienteVO documento : documentos) {
					if(documento.getAnamnese() != null) {
						try {
							listaDadosRelatorio.add(ambulatorioFacade.retornarRelatorioAnamneseEvolucao(atd.getConsulta().getNumero(), DominioAnamneseEvolucao.A));
						} catch (BaseException  e) {
							this.apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
						}
					}
					if(documento.getEvolucao() != null) {
						try {
							listaDadosRelatorio.add(ambulatorioFacade.retornarRelatorioAnamneseEvolucao(atd.getConsulta().getNumero(), DominioAnamneseEvolucao.E));
						} catch (BaseException  e) {
							this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
						}
					}
				}
			}
		}
		
		return listaDadosRelatorio;
	}
	
    public List<RelatorioAnamneseEvolucaoVO> processaDocumentosRelatorioSelecionados() {
		
		List<RelatorioAnamneseEvolucaoVO> listaDadosRelatorio = new ArrayList<RelatorioAnamneseEvolucaoVO>(0);
		for(Map.Entry<AghAtendimentos, Boolean> atd : atendimentosSelecionados.entrySet()) {
			if(atd.getValue()) {
				if(atd.getKey().getConsulta() != null) {
					List<DocumentosPacienteVO> documentos = ambulatorioFacade.obterListaDocumentosPacienteAnamneseEvolucao(atd.getKey().getConsulta().getNumero());
					for(DocumentosPacienteVO documento : documentos) {
						if(documento.getAnamnese() != null) {
							try {
								RelatorioAnamneseEvolucaoVO vo = ambulatorioFacade.retornarRelatorioAnamneseEvolucao(atd.getKey().getConsulta().getNumero(), DominioAnamneseEvolucao.A); 
								vo.setDtAtendimento(atd.getKey().getDthrInicio());
								listaDadosRelatorio.add(vo);
							} catch (BaseException  e) {
								e.getMessage();
								this.apresentarMsgNegocio(Severity.ERROR,e.getLocalizedMessage());
							}
						}
						if(documento.getEvolucao() != null) {
							try {
								RelatorioAnamneseEvolucaoVO vo = ambulatorioFacade.retornarRelatorioAnamneseEvolucao(atd.getKey().getConsulta().getNumero(), DominioAnamneseEvolucao.E); 
								vo.setDtAtendimento(atd.getKey().getDthrInicio());
								listaDadosRelatorio.add(vo);
							} catch (BaseException  e) {
								e.getMessage();
								this.apresentarMsgNegocio(Severity.ERROR, e.getLocalizedMessage());
							}
						}
					}
				}
			}
		}
		final BeanComparator dtInicioSorter = new BeanComparator("dtAtendimento", new ReverseComparator(new NullComparator(false)));
		Collections.sort(listaDadosRelatorio, dtInicioSorter);
		return listaDadosRelatorio;
	}
	
	public String relatorioAtoAnestesico(Integer numeroConsulta){
		try {
			mbcFichaAnestesia = blocoCirurgicoFacade.obterMbcFichaAnestesiaByConsulta(numeroConsulta);
			Boolean docAssinado = prontuarioOnlineFacade.verificarSeDocumentoAtoAnestesicoAssinado(Integer.parseInt(mbcFichaAnestesia.getSeq().toString()));
			if(docAssinado){
				seqVersaoDocAssinado = prontuarioOnlineFacade.chamarDocCertifFicha(Integer.parseInt(mbcFichaAnestesia.getSeq().toString()));
				if (seqVersaoDocAssinado != null){
					visualizarDocumentoController.setSeqAghVersaoDocumento(seqVersaoDocAssinado);
					visualizarDocumentoController.setOrigem(POL_AMBULATORIO);
					return DOCUMENTO_ASSINADO_CONSUL_AMBULATORIO_POL;
				}
			}else{
				relatorioAtosAnestesicosController.setVoltarPara(POL_AMBULATORIO);
				relatorioAtosAnestesicosController.setSeqMbcFichaAnestesia(mbcFichaAnestesia.getSeq());
				relatorioAtosAnestesicosController.setvSessao("-1");
				return RELATORIO_ATOS_ANESTESICOS_CONSULTA_AMBULATORIO;
			}
			
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} 
		
		return null;
	}
	
	public String relatorioAltasAmbulatoriais(AghAtendimentos atendimentoSelec){
		this.seqMamAltaSumario = atendimentoSelec.getMamAltasSumarios().get(0).getSeq();
		imprimeAltaAmbulatorialPolController.setSeqMamAltaSumario(seqMamAltaSumario);
		imprimeAltaAmbulatorialPolController.setVoltarPara(POL_AMBULATORIO);
		return VISUALIZAR_RELATORIO_ALTA_AMBULATORIAL_AMBULATORIO;
		
	}
	
	public Boolean atendimentoAmbulatorialComPrescricao(AghAtendimentos atendimento){
		if(DominioOrigemAtendimento.getOrigensDePrescricaoAmbulatorial().contains(atendimento.getOrigem())){
			return !prescricaoMedicaFacade.pesquisarPrescricoesMedicaNaoPendentes(atendimento.getSeq(), null).isEmpty();
		}
		return Boolean.FALSE;
	}	
	
	public Boolean habilitarBotaoVisualizar(){
		//Se tiver a permissaoAcessoAdminPOL, NAO pode habilitar
		return !temPermissaoAcessoAdminPOL  && relatorioTemInformacao();
	}
	
	private Boolean relatorioTemInformacao(){
		if(readOnly) {
			return verificaSeExisteDocumentosRelatorioSelecionados();
		}
		else {
			if(dtInicial != null && dtFinal != null) {
				if(DateUtil.validaDataTruncadaMaiorIgual(dtFinal, dtInicial)) {
					List<AghAtendimentos> atendimentos = this.aghuFacade.pesquisarAtendimentoAmbPorPacCodigoEntreDtInicioEDtFim((Integer) itemPOL.getParametros().get(NodoPOLVO.COD_PACIENTE), dtInicial, dtFinal);
					if(atendimentos == null || atendimentos.isEmpty()){
						return false;
					}
					return verificaSeExisteDocumentosRelatorioEntreDatas(atendimentos);
				}
				
			}
		}
		return false;
	}
	
	private Boolean verificaSeExisteDocumentosRelatorioEntreDatas(List<AghAtendimentos> atds) {
				
		for(AghAtendimentos atd : atds) {
			if(atd.getConsulta() != null) {
				if (ambulatorioFacade.verificarSeExiteListaDocumentosPacienteAnamneseEvolucao(atd.getConsulta().getNumero())){
					return true;
				}
			}
		}
		
		return false;
	}
	
	private Boolean verificaSeExisteDocumentosRelatorioSelecionados() {
		for(Map.Entry<AghAtendimentos, Boolean> atd : atendimentosSelecionados.entrySet()) {
			if(atd.getValue()) {
				if(atd.getKey().getConsulta() != null) {
					if (ambulatorioFacade.verificarSeExiteListaDocumentosPacienteAnamneseEvolucao(atd.getKey().getConsulta().getNumero())){
						return true;
					}
				}
			}
		}
		return false;
	}
	
	public boolean habilitarBotaoControle(){
		if (this.selecionado == null){
			return false;
		}
		else{
			List<EcpHorarioControle> listaControle = controlePacienteFacade.listarHorarioControlePorConsulta(this.selecionado.getConsulta().getNumero());
			return listaControle != null && !listaControle.isEmpty();
		}
	}

	public String getDescricaoConsulta() {
		return descricaoConsulta;
	}

	public void setDescricaoConsulta(String descricaoConsulta) {
		this.descricaoConsulta = descricaoConsulta;
	}

	public String getAtendimentos() {
		return atendimentos;
	}

	public void setAtendimentos(String atendimentos) {
		this.atendimentos = atendimentos;
	}

	public Date getDtInicial() {
		return dtInicial;
	}

	public void setDtInicial(Date dtInicial) {
		this.dtInicial = dtInicial;
	}

	public Date getDtFinal() {
		return dtFinal;
	}

	public void setDtFinal(Date dtFinal) {
		this.dtFinal = dtFinal;
	}

	public Boolean getReadOnly() {
		return readOnly;
	}

	public void setReadOnly(Boolean readOnly) {
		this.readOnly = readOnly;
	}

	public Boolean getReadOnlyDataTable() {
		return readOnlyDataTable;
	}

	public void setReadOnlyDataTable(Boolean readOnlyDataTable) {
		this.readOnlyDataTable = readOnlyDataTable;
	}

	public MbcFichaAnestesias getMbcFichaAnestesia() {
		return mbcFichaAnestesia;
	}

	public void setMbcFichaAnestesia(MbcFichaAnestesias mbcFichaAnestesia) {
		this.mbcFichaAnestesia = mbcFichaAnestesia;
	}

	public Integer getSeqVersaoDocAssinado() {
		return seqVersaoDocAssinado;
	}

	public void setSeqVersaoDocAssinado(Integer seqVersaoDocAssinado) {
		this.seqVersaoDocAssinado = seqVersaoDocAssinado;
	}

	public Boolean getSelecionarTodos() {
		return selecionarTodos;
	}

	public void setSelecionarTodos(Boolean selecionarTodos) {
		this.selecionarTodos = selecionarTodos;
	}
	public Long getSeqMamAltaSumario() {
		return seqMamAltaSumario;
	}

	public void setSeqMamAltaSumario(Long seqMamAltaSumario) {
		this.seqMamAltaSumario = seqMamAltaSumario;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

	public DynamicDataModel<AghAtendimentos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AghAtendimentos> dataModel) {
		this.dataModel = dataModel;
	}

	public Integer getProntuario() {
		return prontuario;
	}

	public void setProntuario(Integer prontuario) {
		this.prontuario = prontuario;
	}

	public String getNomeEspecialidade() {
		return nomeEspecialidade;
	}

	public void setNomeEspecialidade(String nomeEspecialidade) {
		this.nomeEspecialidade = nomeEspecialidade;
	}

	public Map<AghAtendimentos, Boolean> getAtendimentosSelecionados() {
		return atendimentosSelecionados;
	}

	public void setAtendimentosSelecionados(
			Map<AghAtendimentos, Boolean> atendimentosSelecionados) {
		this.atendimentosSelecionados = atendimentosSelecionados;
	}

	public List<AghAtendimentos> getAllChecked() {
		return allChecked;
	}

	public void setAllChecked(List<AghAtendimentos> allChecked) {
		this.allChecked = allChecked;
	}

	public NodoPOLVO getItemPOL() {
		return itemPOL;
	}

	public void setItemPOL(NodoPOLVO itemPOL) {
		this.itemPOL = itemPOL;
	}

	public AghAtendimentos getSelecionado() {
		return selecionado;
	}

	public void setSelecionado(AghAtendimentos selecionado) {
		this.selecionado = selecionado;
	}

	public boolean isKeepDescricao() {
		return keepDescricao;
	}

	public void setKeepDescricao(boolean keepDescricao) {
		this.keepDescricao = keepDescricao;
	}
}