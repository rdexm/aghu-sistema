package br.gov.mec.aghu.prescricaomedica.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.enterprise.inject.Instance;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.ambulatorio.business.IAmbulatorioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.internacao.action.PesquisaCidController;
import br.gov.mec.aghu.internacao.vo.PesquisaCidVO;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghCid;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.MamDiagnostico;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.DiagnosticosPacienteVO;
import br.gov.mec.aghu.prescricaomedica.vo.DiagnosticosPacienteVO.Acao;
import br.gov.mec.aghu.recursoshumanos.Pessoa;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.business.SelectionQualifier;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class DiagnosticosPacienteController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final Log LOG = LogFactory.getLog(DiagnosticosPacienteController.class);

	/**
	 * 
	 */
	private static final long serialVersionUID = -4699252740112913307L;

	private static final String PAGE_PESQUISA_CID = "internacao-pesquisaCid";

	private static final String PAGE_MANTER_DIAGNOSTICOS_PACIENTE = "manterDiagnosticosPaciente";

	private static final String PAGE_PESQUISAR_LISTA_PACIENTES_INTERNADOS = "prescricaomedica-pesquisarListaPacientesInternados";

	// private static final String PAGE_VERIFICA_PRESCRICAO_MEDICA =
	// "prescricaomedica-verificaPrescricaoMedica";

	// private static final String PAGE_ARVORE_POL = "arvorePOL"; // TODO
	// ARQUITETURA INTEGRAR POL /paciente/prontuarioonline/arvorePOL.xhtml

	// private static final String PAGE_INCLUIR_NOTAS_POL = "incluirNotasPol";
	// // TODO ARQUITETURA INTEGRAR POL
	// /paciente/prontuarioonline/incluirNotasPOLList.xhtml

	private Boolean comPendencia = Boolean.FALSE;
	

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IAmbulatorioFacade ambulatorioFacade;

	@EJB
	private IAghuFacade aghuFacade;

	private Pessoa pessoa;

	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@Inject
	private ListaPacientesInternadosController listaPacientesInternadosController;

	private RapServidores servidor;

	private Integer atendimentoSeq;
	private Integer cidSeq;
	private Integer pacCodigo;

	private MamDiagnostico mamDiagnostico = new MamDiagnostico();
	private DiagnosticosPacienteVO vo = new DiagnosticosPacienteVO();
	private DiagnosticosPacienteVO voParaExcluir = new DiagnosticosPacienteVO();

	private List<DiagnosticosPacienteVO> listaVO = new ArrayList<DiagnosticosPacienteVO>();
	private List<MamDiagnostico> listaMamDiagnosticosAtivos = new ArrayList<MamDiagnostico>();
	private List<MamDiagnostico> listaMamDiagnosticosResolvidos = new ArrayList<MamDiagnostico>();
	private AghCid aghCid;
	private String complemento;
	private Date dataInicio;
	private Date resolvidoEm;
	private AghAtendimentos atendimento;
	private AipPacientes paciente;
	private boolean editMode = false;
	private boolean resolveMode = false;
	private boolean confirmaVoltar = false;
	private boolean temMudancasNaTela = false;
	private boolean edicaoHabilitada = true;
	private int currentLine = 0;

	private static DiagnosticosPacienteVOComparator classComparator = new DiagnosticosPacienteVOComparator();

	private static CidComparator cidComparator = new CidComparator();

	private String voltarPara;

	@Inject
	@SelectionQualifier
	private Instance<PesquisaCidVO> pesquisaCidRetorno;
	
	@Inject
	private PesquisaCidController pesquisaCidController;
	
	private DiagnosticosPacienteVO voParaEditarSelecionado;
	private Integer linhaSelecionada;

	@SuppressWarnings("PMD.NPathComplexity")
	public void carregar() {
	 


		if (this.pesquisaCidRetorno != null && !isCidNaoEditavel()/* && aghCid == null*/) {
			PesquisaCidVO pesquisaCidVO = pesquisaCidRetorno.get();
			if (aghCid == null) {
				this.aghCid = pesquisaCidVO.getCid();
			}
			this.cidSeq = aghCid != null ? aghCid.getSeq() : null; 
			this.cidSeq = aghCid != null ? aghCid.getSeq() : null; 
		}

		this.setServidor(this.servidorLogadoFacade.obterServidorLogado());
		if (this.getAtendimentoSeq() != null) {
			Enum[] inner = {AghAtendimentos.Fields.PACIENTE};
			setAtendimento(aghuFacade.obterAghAtendimentoPorChavePrimaria(this.getAtendimentoSeq(), inner, null));
		}
		// preencher o cid
		if (this.cidSeq != null) {
			this.setAghCid(this.aghuFacade.obterAghCidsPorChavePrimaria(this.cidSeq));
			this.selecionouCid();
		} else {
			if (getAtendimento() == null && getPacCodigo() != null) {
				setPaciente(pacienteFacade.obterPacientePorCodigo(getPacCodigo()));
			} else if (getAtendimento() != null && getPacCodigo() == null) {
				paciente = getAtendimento().getPaciente();
			} else {
				apresentarMsgNegocio(Severity.ERROR, "ATENDIMENTO_NULO");
				LOG.warn("ATENDIMENTO_NULO");
				return;
			}

			// // carregar a lista de Atendimentos
			try {
				setListaMamDiagnosticosAtivos(ambulatorioFacade.buscarDiagnosticosAtivosPaciente(paciente.getCodigo()));
				setListaMamDiagnosticosResolvidos(ambulatorioFacade.buscarDiagnosticosResolvidosPaciente(paciente.getCodigo()));
			} catch (ApplicationBusinessException e) {
				this.apresentarExcecaoNegocio(e);
			}
			for (MamDiagnostico diagnostico : listaMamDiagnosticosAtivos) {
				DiagnosticosPacienteVO maVO = new DiagnosticosPacienteVO();
				maVO.setMamDiagnostico(diagnostico);
				maVO.setAcao(Acao.NENHUM);
				maVO.setSalvoNoBanco(true);
				if (!this.listaVO.contains(maVO)) {
					this.listaVO.add(maVO);
				}

			}
			setConfirmaVoltar(false);
			setTemMudancasNaTela(false);
		}
		// return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
	
	}

	public String adicionar() {
		try {
			ambulatorioFacade.validarInsercaoDiagnostico(montaNovoDiagnostico());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
			carregar();
			return null;
		}

		atribuirValoresParaVO();
		vo.setAcao(Acao.ADICIONAR);
		vo.setSalvoNoBanco(false);
		listaVO.add(vo);
		Collections.sort(this.listaVO, classComparator);
		setTemMudancasNaTela(true);
		limparCamposEdicao();
		return null;
	}

	public String resolverItem() {
		cancelarEdicao();
		editar();
		setEditMode(false);
		setResolveMode(true);
		//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
		carregar();
		return null;
	}

	public String definirExclusao() {
		if (voParaExcluir.isSalvoNoBanco()) {
			voParaExcluir.setAcao(Acao.EXCLUIR);
			List<DiagnosticosPacienteVO> listaExclusao = new ArrayList<DiagnosticosPacienteVO>();
			listaExclusao.add(voParaExcluir);
			try {
				ambulatorioFacade.salvarListaDiganosticosPaciente(listaExclusao);
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_DIAGNOSTICO_EXCLUIDO_SUCESSO");
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}
		}
		return excluir(voParaExcluir);
		//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
	}

	public String excluir(DiagnosticosPacienteVO voParaExcluir) {
		cancelarEdicao();
		listaVO.remove(voParaExcluir);
		//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
		carregar();
		return null;
	}

	public void cancelaVoltar() {
		setConfirmaVoltar(false);
	}

	public String editar() {
		comPendencia = Boolean.TRUE;
		desabilitaEdicaoItens();
		this.currentLine = linhaSelecionada;
		atribuirValoresDoVO();
		setEditMode(true);
		voParaEditarSelecionado.setEditando(true);
				return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
		/*carregar();
		return null;*/
	}

	private void desabilitaEdicaoItens(){
		for(DiagnosticosPacienteVO voIn:listaVO){
			voIn.setEditando(Boolean.FALSE);
		}
	}
	
	public String alterar() {
		
		try {
			ambulatorioFacade.validarAtualizacaoDiagnostico(montaNovoDiagnostico(), getServidor());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
			carregar();
			return null;
		}

		atribuirValoresParaVO();
		if (!Acao.ADICIONAR.equals(vo.getAcao())) {
			vo.setAcao(Acao.ALTERAR);
		}
		vo.setEditando(false);
		listaVO.set(currentLine, vo);
		Collections.sort(this.listaVO, classComparator);
		cancelarEdicao();
		setTemMudancasNaTela(true);
		comPendencia = Boolean.TRUE;
		//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
		carregar();
		return null;
	}

	public String resolver() {
		try {
			ambulatorioFacade.validarAtualizacaoDiagnostico(montaNovoDiagnostico(), getServidor());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
			return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
			/*carregar();
			return null;*/
		}
		atribuirValoresParaVO();
		if (!Acao.ADICIONAR.equals(vo.getAcao())) {
			vo.setAcao(Acao.RESOLVER);
		}
		vo.setEditando(false);
		listaVO.set(currentLine, vo);
		Collections.sort(this.listaVO, classComparator);
		cancelarEdicao();
		setTemMudancasNaTela(true);
		return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
		/*carregar();
		return null;*/
	}

	public String cancelarEdicao() {
		comPendencia = Boolean.FALSE;
		if (!listaVO.isEmpty() && currentLine > -1) {
			listaVO.get(currentLine).setEditando(false);
		}
		limparCamposEdicao();
		setEditMode(false);
		setResolveMode(false);
		//pesquisaCidRetorno = null;
		pesquisaCidController.limparTela();
		//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
		carregar();
		return null;
	}

	public void trocarDeAba() {
		if (this.edicaoHabilitada) {
			this.edicaoHabilitada = false;
		} else {
			this.edicaoHabilitada = true;
		}
		cancelarEdicao();
	}

	private void limparCamposEdicao() {
		this.vo = new DiagnosticosPacienteVO();
		this.mamDiagnostico = new MamDiagnostico();
		this.aghCid = null;
		this.cidSeq = null;
		this.currentLine = -1;
	}

	private void atribuirValoresParaVO() {
		MamDiagnostico diagnostico = montaNovoDiagnostico();
		vo.setMamDiagnostico(diagnostico);
		vo.setServidor(getServidor());

	}

	private MamDiagnostico montaNovoDiagnostico() {
		MamDiagnostico diagnostico = new MamDiagnostico();
		diagnostico.setCid(getMamDiagnostico().getCid());
		diagnostico.setComplemento(StringUtils.trimToNull(getMamDiagnostico().getComplemento()));
		diagnostico.setData(getMamDiagnostico().getData() != null ? new Date(getMamDiagnostico().getData().getTime()) : null);
		diagnostico.setDataFim(getMamDiagnostico().getDataFim() != null ? new Date(getMamDiagnostico().getDataFim().getTime()) : null);
		diagnostico.setSeq(getMamDiagnostico().getSeq());
		diagnostico.setPaciente(getPaciente());
		diagnostico.setAtendimento(getAtendimento());
		return diagnostico;
	}

	private void atribuirValoresDoVO() {
		setAghCid(voParaEditarSelecionado.getMamDiagnostico().getCid());
		getMamDiagnostico().setCid(voParaEditarSelecionado.getMamDiagnostico().getCid());
		getMamDiagnostico().setComplemento(voParaEditarSelecionado.getMamDiagnostico().getComplemento());
		getMamDiagnostico().setData(voParaEditarSelecionado.getMamDiagnostico().getData() != null ? new Date(voParaEditarSelecionado.getMamDiagnostico().getData().getTime()) : null);
		getMamDiagnostico().setDataFim(voParaEditarSelecionado.getMamDiagnostico().getDataFim() != null ? new Date(voParaEditarSelecionado.getMamDiagnostico().getDataFim().getTime()) : null);
		getMamDiagnostico().setSeq(voParaEditarSelecionado.getMamDiagnostico().getSeq());
		vo.setAcao(voParaEditarSelecionado.getAcao());
		vo.setSalvoNoBanco(voParaEditarSelecionado.isSalvoNoBanco());
	}

	/**
	 * Salva as alterações
	 * 
	 * @return
	 */
	public String salvar() {
		Boolean pend = verificarPendencias();
		if(pend){
			openDialog("modalPendenciasNaoSalvasWG");
		}else{
			try {
				ambulatorioFacade.salvarListaDiganosticosPaciente(this.listaVO);
				comPendencia = Boolean.FALSE;
				apresentarMsgNegocio(Severity.INFO, "MENSAGEM_LISTA_DIAGNOSTICO_SALVA_SUCESSO");
				this.limpar();
			} catch (BaseException e) {
				this.apresentarExcecaoNegocio(e);
			}
			//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
			//carregar();
		}
		return null;
	}

	/**
	 * Retorna os valores da tela ao conteúdo do banco de dados
	 */
	public String limpar() {
		setListaVO(new ArrayList<DiagnosticosPacienteVO>());
		cancelarEdicao();
		comPendencia = Boolean.FALSE;
		//carregar();
		//return PAGE_MANTER_DIAGNOSTICOS_PACIENTE;
		//carregar();
		return null;
	}

	/**
	 * Método da suggestion box para pesquisa de CIDs a incluir na lista Ignora
	 * a pesquisa caso o parametro seja o próprio valor selecionado
	 * anteriormente (contorna falha de pesquisa múltipla na suggestion box)
	 * 
	 * @param parametro
	 * @return
	 */
	public List<AghCid> pesquisarCids(String parametro) {
		String paramString = (String) parametro;
		// this.debug("ManterDiagnosticosAtendimentoController.pesquisarCids()(): parametro = ["
		// + parametro + "].");
		List<AghCid> result = new ArrayList<AghCid>();
		if (this.getAghCid() == null
				|| !(StringUtils.equalsIgnoreCase(paramString, this.getAghCid().getCodigo()) || StringUtils.equalsIgnoreCase(paramString, this.getAghCid().getDescricao()))) {
			result = aghuFacade.obterCidPorNomeCodigoAtivaPaginado(paramString);
		} else {
			// adiciona a especialidade selecionada para nao mostrar mensagens
			// erradas na tela
			result.add(this.getAghCid());
		}
		Collections.sort(result, cidComparator);
		return this.returnSGWithCount(result,pesquisarCidsCount(parametro));
	}

	public Long pesquisarCidsCount(String parametro) {
		return aghuFacade.obterCidPorNomeCodigoAtivaCount((String) parametro);
	}

	public void selecionouCid() {
		getMamDiagnostico().setCid(this.getAghCid());
	}

	public String verificaPendencias() {
		if (this.getAghCid() != null || StringUtils.isNotEmpty(mamDiagnostico.getComplemento()) || mamDiagnostico.getData() != null || mamDiagnostico.getDataFim() != null) {
			this.setConfirmaVoltar(true);
		}
		if (this.isTemMudancasNaTela()) {
			setConfirmaVoltar(true);
		}
		if (this.isConfirmaVoltar()) {
			super.openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}
		return this.voltar();
	}

	public String voltar() {
		comPendencia= verificarPendencias();
		
		if(comPendencia){
			openDialog("modalConfirmacaoPendenciaWG");
			return null;
		}else{
			return voltarOK();
		}
	}

	private Boolean verificarPendencias() {
		if(aghCid != null 
				|| (mamDiagnostico.getComplemento() != null && !"".equals(mamDiagnostico.getComplemento()))
				|| mamDiagnostico.getData() != null 
				|| mamDiagnostico.getDataFim() != null ){
			return Boolean.TRUE;
		}
		
		if(comPendencia){
			for(DiagnosticosPacienteVO voPend: listaVO){
				if(voPend.isEditando()){
					return Boolean.TRUE;
				}
			}
		}
		return Boolean.FALSE;
	}

	public String voltarOK() {
		cancelarEdicao();
		setEdicaoHabilitada(true);

		String returnValue = PAGE_PESQUISAR_LISTA_PACIENTES_INTERNADOS;

		if (voltarPara != null) {
			returnValue = voltarPara;
		}

		this.resetarTela();
		
		if(PAGE_PESQUISAR_LISTA_PACIENTES_INTERNADOS.equals(voltarPara)){
			try {
				this.listaPacientesInternadosController.pesquisar();
			} catch (BaseException e) {
				apresentarExcecaoNegocio(e);
			}
		} 

		return returnValue;
	}

	public void resetarTela() {
		this.servidor = null;
		this.atendimentoSeq = null;
		this.cidSeq = null;
		this.pacCodigo = null;
		this.mamDiagnostico = new MamDiagnostico();
		this.vo = new DiagnosticosPacienteVO();
		this.voParaExcluir = new DiagnosticosPacienteVO();
		this.listaVO = new ArrayList<DiagnosticosPacienteVO>();
		this.listaMamDiagnosticosAtivos = new ArrayList<MamDiagnostico>();
		this.listaMamDiagnosticosResolvidos = new ArrayList<MamDiagnostico>();
		this.aghCid = null;
		this.complemento = null;
		this.dataInicio = null;
		this.resolvidoEm = null;
		this.atendimento = null;
		this.paciente = null;
		this.editMode = false;
		this.resolveMode = false;
		this.confirmaVoltar = false;
		this.temMudancasNaTela = false;
		this.edicaoHabilitada = true;
		this.currentLine = 0;
	}

	public String pesquisaCidCapitulo() {
		return PAGE_PESQUISA_CID;
	}

	public void setPrescricaoMedicaFacade(IPrescricaoMedicaFacade prescricaoMedicaFacade) {
		this.prescricaoMedicaFacade = prescricaoMedicaFacade;
	}

	public IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return this.prescricaoMedicaFacade;
	}

	public void setAtendimentoSeq(Integer atendimentoSeq) {
		this.atendimentoSeq = atendimentoSeq;
	}

	public Integer getAtendimentoSeq() {
		return this.atendimentoSeq;
	}

	public AghCid getAghCid() {
		return aghCid;
	}

	public void setAghCid(AghCid aghCid) {
		this.aghCid = aghCid;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public RapServidores getServidor() {
		return this.servidor;
	}

	public void setPessoa(Pessoa pessoa) {
		this.pessoa = pessoa;
	}

	public Pessoa getPessoa() {
		return this.pessoa;
	}

	public void setCidSeq(Integer cidSeq) {
		this.cidSeq = cidSeq;
	}

	public Integer getCidSeq() {
		return this.cidSeq;
	}

	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public boolean isConfirmaVoltar() {
		return this.confirmaVoltar;
	}

	public void setPaciente(AipPacientes paciente) {
		this.paciente = paciente;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setListaMamDiagnosticosAtivos(List<MamDiagnostico> listaMamDiagnosticosAtivos) {
		this.listaMamDiagnosticosAtivos = listaMamDiagnosticosAtivos;
	}

	public List<MamDiagnostico> getListaMamDiagnosticosAtivos() {
		return listaMamDiagnosticosAtivos;
	}

	public void setListaMamDiagnosticosResolvidos(List<MamDiagnostico> listaMamDiagnosticosResolvidos) {
		this.listaMamDiagnosticosResolvidos = listaMamDiagnosticosResolvidos;
	}

	public List<MamDiagnostico> getListaMamDiagnosticosResolvidos() {
		return listaMamDiagnosticosResolvidos;
	}

	public void setEditMode(boolean editMode) {
		this.editMode = editMode;
	}

	public boolean isEditMode() {
		return editMode;
	}

	public void setComplemento(String complemento) {
		this.complemento = complemento;
	}

	public String getComplemento() {
		return complemento;
	}

	public void setDataInicio(Date dataInicio) {
		this.dataInicio = dataInicio;
	}

	public Date getDataInicio() {
		return dataInicio;
	}

	public void setResolvidoEm(Date resolvidoEm) {
		this.resolvidoEm = resolvidoEm;
	}

	public Date getResolvidoEm() {
		return resolvidoEm;
	}

	public boolean isCidNaoEditavel() {
		if (isEdicaoHabilitada()) {
			return isEditMode();
		} else {
			return true;
		}
	}

	public void setMamDiagnostico(MamDiagnostico mamDiagnostico) {
		this.mamDiagnostico = mamDiagnostico;
	}

	public MamDiagnostico getMamDiagnostico() {
		return mamDiagnostico;
	}

	public void setEdicaoHabilitada(boolean edicaoHabilitada) {
		this.edicaoHabilitada = edicaoHabilitada;
	}

	public boolean isEdicaoHabilitada() {
		return edicaoHabilitada;
	}

	public void setListaVO(List<DiagnosticosPacienteVO> listaVO) {
		this.listaVO = listaVO;
	}

	public List<DiagnosticosPacienteVO> getListaVO() {
		return listaVO;
	}

	public void setVoParaExcluir(DiagnosticosPacienteVO voParaExcluir) {
		this.voParaExcluir = voParaExcluir;
	}

	public DiagnosticosPacienteVO getVoParaExcluir() {
		return voParaExcluir;
	}

	public void setAtendimento(AghAtendimentos atendimento) {
		this.atendimento = atendimento;
	}

	public AghAtendimentos getAtendimento() {
		return atendimento;
	}

	public void setTemMudancasNaTela(boolean temMudancasNaTela) {
		this.temMudancasNaTela = temMudancasNaTela;
	}

	public boolean isTemMudancasNaTela() {
		return temMudancasNaTela;
	}

	public void setResolveMode(boolean resolveMode) {
		this.resolveMode = resolveMode;
	}

	public boolean isResolveMode() {
		return resolveMode;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public String getVoltarPara() {
		return voltarPara;
	}

//	private static class MamDiagnosticoComparator implements Comparator<MamDiagnostico> {
//		@Override
//		public int compare(MamDiagnostico a1, MamDiagnostico a2) {
//			int result = 0;
//			if (a1.getData() != null && a2.getData() != null) {
//				result = a1.getData().compareTo(a2.getData());
//			}
//			if (result == 0) {
//				result = a1.getCid().getCodigo().compareTo(a2.getCid().getCodigo());
//			}
//			return result;
//		}
//	}

	private static class DiagnosticosPacienteVOComparator implements Comparator<DiagnosticosPacienteVO> {
		@Override
		public int compare(DiagnosticosPacienteVO a1, DiagnosticosPacienteVO a2) {
			int result = 0;
			if (a1.getMamDiagnostico().getData() != null && a2.getMamDiagnostico().getData() != null) {
				result = a1.getMamDiagnostico().getData().compareTo(a2.getMamDiagnostico().getData());
			}
			if (result == 0) {
				result = a1.getMamDiagnostico().getCid().getCodigo().compareTo(a2.getMamDiagnostico().getCid().getCodigo());
			}
			return result;
		}
	}

	private static class CidComparator implements Comparator<AghCid> {
		@Override
		public int compare(AghCid a1, AghCid a2) {
			int result = a1.getCodigo().compareToIgnoreCase(a2.getCodigo());
			return result;
		}
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public DiagnosticosPacienteVO getVoParaEditarSelecionado() {
		return voParaEditarSelecionado;
	}

	public void setVoParaEditarSelecionado(
			DiagnosticosPacienteVO voParaEditarSelecionado) {
		this.voParaEditarSelecionado = voParaEditarSelecionado;
	}

	public Integer getLinhaSelecionada() {
		return linhaSelecionada;
	}

	public void setLinhaSelecionada(Integer linhaSelecionada) {
		this.linhaSelecionada = linhaSelecionada;
	}

}