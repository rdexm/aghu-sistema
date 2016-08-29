package br.gov.mec.aghu.paciente.prontuario.action;

import java.io.IOException;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;
import javax.inject.Inject;
import javax.transaction.SystemException;

import net.sf.jasperreports.engine.JRException;

import org.apache.commons.lang3.SerializationUtils;
import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.AipSolicitacaoProntuarios;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.paciente.cadastro.business.ICadastroPacienteFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;


public class SolicitarProntuarioController extends ActionController implements ActionPaginator{

	private static final long serialVersionUID = -7135401547706759886L;
	private static final Log LOG = LogFactory.getLog(SolicitarProntuarioController.class);
	private static final String REDIRECT_PESQUISAR_SOLICITACAO_PRONTUARIO = "solicitacaoProntuarioList";
	private static final String REDIRECT_IMPRIMIR_SOLICITACAO_PRONTUARIO = "solicitacaoProntuarioPdf";

	
	@EJB
	private IPacienteFacade pacienteFacade;

	@EJB
	private ICadastroPacienteFacade cadastroPacienteFacade;
	
	@EJB
	private IAghuFacade aghuFacade;
	
	@Inject
	private SolicitarProntuarioPaginatorController solicitarProntuarioPaginatorController;
	
	@Inject
	private RelatorioSolicitacaoProntuarioController relatorioSolicitacaoProntuarioController;
	
	private AghEspecialidades especialidade;

	private AipPacientes aipPaciente;

	private AipSolicitacaoProntuarios solicitacaoProntuario = new AipSolicitacaoProntuarios();

	// Atributo para armazenar a lista de Pacientes
	private List<AipPacientes> pacienteList = new ArrayList<AipPacientes>();

	// Atributo temporário para armazenar a lista de Pacientes
	private List<AipPacientes> pacienteTempList = new ArrayList<AipPacientes>();
	
	// Atributo para armazenar a lista de Pacientes adicionados que não foram salvos ainda
	private Boolean pacientesAlteradosNaoSalvos = false;
	
	// Atributo temporário para armazenar a lista de Pacientes a serem removidos de uma solicitacao
	private List<AipPacientes> pacienteRemoveList = new ArrayList<AipPacientes>();

	private Boolean operacaoConcluida = false;


	/**
	 * Comparator usado entre as listas de Pacientes da solicitação de
	 * prontuário e da tela.
	 */
	private static final Comparator<AipPacientes> COMPARATOR_PACIENTES_NOME = new Comparator<AipPacientes>() {
		@Override
		public int compare(AipPacientes o1, AipPacientes o2) {
			return o1.getNome().compareTo(o2.getNome());
		}
	};
	
	public void iniciarInclusao(){
		this.solicitacaoProntuario = new AipSolicitacaoProntuarios();
		this.pacienteTempList = new ArrayList<AipPacientes>();
		this.pacienteList = new ArrayList<AipPacientes>();
		this.inicio();
	}
	
	public void iniciarEdicao(AipSolicitacaoProntuarios solicitacaoProntuario){
		this.solicitacaoProntuario = new AipSolicitacaoProntuarios();
		this.solicitacaoProntuario = solicitacaoProntuario;
		ArrayList<AipPacientes> listaPacientes = new ArrayList<AipPacientes>();
		for(AipPacientes paciente: solicitacaoProntuario.getAipPacientes()){
			listaPacientes.add(paciente);
		}
		Collections.sort(listaPacientes, COMPARATOR_PACIENTES_NOME);
		
		solicitacaoProntuario.getAipPacientes().clear();
		solicitacaoProntuario.getAipPacientes().addAll(listaPacientes);
		
		this.pacienteTempList = new ArrayList<AipPacientes>();
		this.pacienteTempList.clear();
		this.pacienteTempList.addAll(listaPacientes);

		this.pacienteList = new ArrayList<AipPacientes>();
		this.pacienteList.clear();
		this.pacienteList.addAll(this.pacienteTempList);
		this.inicio();
	}

	private void inicio() {
		pacientesAlteradosNaoSalvos = false;
	}
	
	
	@Override
	public Long recuperarCount() {
		if (this.solicitacaoProntuario.getAipPacientes() == null
				|| this.solicitacaoProntuario.getAipPacientes().size() == 0) {
			return 0L;
		} else {
			return Long.valueOf(this.solicitacaoProntuario.getAipPacientes().size());
		}
	}

	@Override
	@SuppressWarnings("unchecked")
	public List recuperarListaPaginada(Integer firstResult, Integer maxResult,
			String orderProperty, boolean asc) {
		if (this.solicitacaoProntuario.getAipPacientes() == null
				|| this.solicitacaoProntuario.getAipPacientes().size() == 0) {
			return new ArrayList();
		} else {
			Object[] pacientesList = this.solicitacaoProntuario
					.getAipPacientes().toArray();
			return Arrays.asList(pacientesList);
		}
	}
	
	
	
	
	public String imprimirDetalhe(){
		try {
			this.relatorioSolicitacaoProntuarioController.print(this.solicitacaoProntuario, "detalhe");
			return REDIRECT_IMPRIMIR_SOLICITACAO_PRONTUARIO;
			
		} catch (JRException | SystemException | IOException e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
			return null;
		}
	}
	
	public String imprimirDiretoDetalhe(){
		try {
			this.relatorioSolicitacaoProntuarioController.impressaoDireta(solicitacaoProntuario, "detalhe");
			return REDIRECT_IMPRIMIR_SOLICITACAO_PRONTUARIO;
		} catch (JRException | SystemException | IOException | ParseException e) {
			this.apresentarMsgNegocio(Severity.ERROR,"ERRO_GERAR_RELATORIO");
			return null;
		}
	}

	public List<AipPacientes> pesquisarPacientesPorProntuario(Object param) {
		return this.pacienteFacade
				.pesquisarPacientesPorProntuario(param == null ? null : param
						.toString());
	}

	public String salvarImprimir() {
		this.salvarRegistro();
		return imprimirDiretoDetalhe();
	}

	public String salvarVisualizar() {
		this.salvarRegistro();
		return imprimirDetalhe();
	}
	
	public String salvar() {
		solicitarProntuarioPaginatorController.reiniciarPaginator();
		final String retorno = salvarRegistro();
		this.limpar();
		return retorno;
	}

	private String salvarRegistro() {
		try {
			cadastroPacienteFacade.validaProntuariosSolicitacao(pacienteList);
			HashSet<AipPacientes> pacientesSet = new HashSet<AipPacientes>();
			for(int i=0; i<pacienteList.size();i++){
				pacientesSet.add(pacienteList.get(i));
			}
			
			this.solicitacaoProntuario.setAipPacientes(pacientesSet);
			boolean create = this.solicitacaoProntuario.getCodigo() == null;
			
			if(!StringUtils.isEmpty(solicitacaoProntuario.getSolicitante())){
				solicitacaoProntuario.setSolicitante(solicitacaoProntuario.getSolicitante().toUpperCase());	
			}
			else {
				solicitacaoProntuario.setSolicitante(null);
			}
			if(!StringUtils.isEmpty(solicitacaoProntuario.getResponsavel())){
				solicitacaoProntuario.setResponsavel(solicitacaoProntuario.getResponsavel().toUpperCase());	
			}
			else {
				solicitacaoProntuario.setResponsavel(null);
			}
			if(!StringUtils.isEmpty(solicitacaoProntuario.getObservacao())){
				solicitacaoProntuario.setObservacao(solicitacaoProntuario.getObservacao().toUpperCase());	
			}
			else {
				solicitacaoProntuario.setObservacao(null);
			}
			
			this.cadastroPacienteFacade
					.persistirSolicitacaoProntuario(this.solicitacaoProntuario, this.pacienteRemoveList);
			
			pacientesAlteradosNaoSalvos = false;
			if (create) {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_CRIACAO_SOLICITACAO_PRONTUARIO",
						this.solicitacaoProntuario.getCodigo());
			} else {
				this.apresentarMsgNegocio(Severity.INFO,
						"MENSAGEM_SUCESSO_EDICAO_SOLICITACAO_PRONTUARIO",
						this.solicitacaoProntuario.getCodigo());
			}
			
		} catch (ApplicationBusinessException e) {
			e.getMessage();
			apresentarExcecaoNegocio(e);
			return null;
		} 

		return REDIRECT_PESQUISAR_SOLICITACAO_PRONTUARIO;
	}

	public List<AghEspecialidades> listarEspecialidadesSolicitacaoProntuario(String paramPesquisa) {
		return aghuFacade.listarEspecialidadesSolicitacaoProntuario(paramPesquisa,false);
	}

	public Long listarEspecialidadesSolicitacaoProntuarioCount(String paramPesquisa) {
		return aghuFacade.listarEspecialidadesSolicitacaoProntuarioCount(paramPesquisa,false);
	}
	
	private void limpar() {
		this.solicitacaoProntuario = new AipSolicitacaoProntuarios();
		this.aipPaciente = null;
		this.pacienteList = new ArrayList<AipPacientes>();
		this.solicitarProntuarioPaginatorController.reiniciarPaginator();
	}

	public String cancelar() {
		LOG.info("Cancelado");
		// ...
		pacientesAlteradosNaoSalvos = false;
		this.limpar();
		return REDIRECT_PESQUISAR_SOLICITACAO_PRONTUARIO;
	}

	public String getNomeEspecialidadeSelecionada() {

		if (this.solicitacaoProntuario.getAghEspecialidades() == null
				|| StringUtils.isBlank(this.solicitacaoProntuario
						.getAghEspecialidades().getNomeEspecialidade())) {
			return "";
		}
		return this.solicitacaoProntuario.getAghEspecialidades()
				.getNomeEspecialidade();

	}

	public String getDescricaoProntuarioSelecionado() {

		if (this.getAipPaciente() == null || this.aipPaciente.getCodigo() == null) {
			return "";
		}
		else {
			return this.getAipPaciente().getNome().toString();
		} 
	}

	public void atribuirEspecialidade() {
		if(solicitacaoProntuario.getAghEspecialidades()!=null){
			this.atribuirEspecialidade(solicitacaoProntuario.getAghEspecialidades());	
		}
	}

	/**
	 * @return the especialidade
	 */
	public AghEspecialidades getEspecialidade() {
		return especialidade;
	}

	/**
	 * @param especialidade
	 *            the especialidade to set
	 */
	public void setEspecialidade(AghEspecialidades especialidade) {
		this.especialidade = especialidade;
	}

	/**
	 * @return the solicitacaoProntuario
	 */
	public AipSolicitacaoProntuarios getSolicitacaoProntuario() {
		return solicitacaoProntuario;
	}

	/**
	 * @param solicitacaoProntuario
	 *            the solicitacaoProntuario to set
	 */
	public void setSolicitacaoProntuario(
			AipSolicitacaoProntuarios solicitacaoProntuario) {
		this.solicitacaoProntuario = solicitacaoProntuario;
	}

	/**
	 * @return the aipPaciente
	 */
	public AipPacientes getAipPaciente() {
		return aipPaciente;
	}

	/**
	 * @param aipPaciente
	 *            the aipPaciente to set
	 */
	public void setAipPaciente(AipPacientes aipPaciente) {
		this.aipPaciente = aipPaciente;
	}


	public Integer getTotalProntuarios() {
		return pacienteList.size();
	}

	/**
	 * @return the pacienteList
	 */
	public List<AipPacientes> getPacienteList() {
		return pacienteList;
	}

	/**
	 * @param pacienteList
	 *            the pacienteList to set
	 */
	public void setPacienteList(List<AipPacientes> pacienteList) {
		this.pacienteList = pacienteList;
	}

	/**
	 * @return the comparatorPacientesNome
	 */
	public static Comparator<AipPacientes> getComparatorPacientesNome() {
		return COMPARATOR_PACIENTES_NOME;
	}

	public boolean isMostrarLinkExcluirEspecialidade() {
		return this.solicitacaoProntuario!= null
				&& this.solicitacaoProntuario.getAghEspecialidades() != null;
	}

	public void limparEspecialidade() {
		this.solicitacaoProntuario.setAghEspecialidades(null);
	}

	public void adicionarProntuario() {
		if (this.aipPaciente != null && aipPaciente.getNome() != null) {
			
			// Se item ainda não existe na lista, adiciona e reordena a mesma.
			if (!this.pacienteTempList.contains(aipPaciente)) {
				
				AipPacientes pac = new AipPacientes();
				pac = (AipPacientes) SerializationUtils.clone(aipPaciente);
				
				pacientesAlteradosNaoSalvos = true;
				
				this.pacienteTempList.add(pac);
				
				if (this.pacienteTempList.size() > 1) {
					Collections.sort(this.pacienteTempList,
							COMPARATOR_PACIENTES_NOME);
				}
			}

			this.operacaoConcluida = true;
			this.aipPaciente = null;

			this.pacienteList.clear();
			this.pacienteList.addAll(this.pacienteTempList);
		}
	}

	/**
	 * @return the operacaoConcluida
	 */
	public Boolean getOperacaoConcluida() {
		return operacaoConcluida;
	}

	/**
	 * @param operacaoConcluida
	 *            the operacaoConcluida to set
	 */
	public void setOperacaoConcluida(Boolean operacaoConcluida) {
		this.operacaoConcluida = operacaoConcluida;
	}

	public void atribuirEspecialidade(AghEspecialidades especialidade) {
		this.solicitacaoProntuario.setAghEspecialidades(especialidade);
	}
	
	public boolean isMostrarLinkExcluirProntuario() {
		return this.aipPaciente != null
				&& this.aipPaciente.getCodigo() != null;
	}

	public void limparProntuario() {
		this.aipPaciente = null;
	}
	
	public List<AipPacientes> pesquisarPacientesPorProntuarioOuCodigo(String strPesquisa) {
		return pacienteFacade.pesquisarPacientesComProntuarioPorProntuarioOuCodigo((String)strPesquisa);
	}
	
	public List<AghEspecialidades> pesquisarSolicitarProntuarioEspecialidade(
			Object strPesquisa) {
		return aghuFacade
				.pesquisarSolicitarProntuarioEspecialidade(strPesquisa == null ? null
						: strPesquisa.toString());
	}
	
	public void excluirPacienteDeSolicitante(Integer codigoPaciente) {
		pacientesAlteradosNaoSalvos = true;
		AipPacientes ds = null;
		for (AipPacientes paciente : pacienteTempList) {
			if (codigoPaciente.equals(paciente.getCodigo())) {
				ds = paciente;
				codigoPaciente = null;
				break;
			}
		}
		
		if (ds != null) {
			this.pacienteRemoveList.add(ds);
			this.pacienteTempList.remove(ds);
		}
		
		this.pacienteList.clear();
		this.pacienteList.addAll(this.pacienteTempList);
		this.aipPaciente = new AipPacientes();
		
		this.operacaoConcluida = true;
	}

	/**
	 * @return the pacienteRemoveList
	 */
	public List<AipPacientes> getPacienteRemoveList() {
		return pacienteRemoveList;
	}

	/**
	 * @param pacienteRemoveList the pacienteRemoveList to set
	 */
	public void setPacienteRemoveList(List<AipPacientes> pacienteRemoveList) {
		this.pacienteRemoveList = pacienteRemoveList;
	}

	public Boolean getPacientesAlteradosNaoSalvos() {
		return pacientesAlteradosNaoSalvos;
	}

	public void setPacientesAlteradosNaoSalvos(Boolean pacientesAlteradosNaoSalvos) {
		this.pacientesAlteradosNaoSalvos = pacientesAlteradosNaoSalvos;
	}
}