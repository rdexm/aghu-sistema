package br.gov.mec.aghu.prescricaomedica.action;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioTipoSolicitacaoConsultoria;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghParametros;
import br.gov.mec.aghu.model.MpmPrescricaoMedica;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class SolicitarConsultoriaController extends ActionController {

	private static final long serialVersionUID = 7422304697897511065L;
	private static final String PAGINA_MANTER_PRESCRICAO_MEDICA = "prescricaomedica-manterPrescricaoMedica";
	private static final Log LOG = LogFactory.getLog(SolicitarConsultoriaController.class);

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private IParametroFacade parametroFacade;
	

	// Parametros passados no request via get (Configuracao no page.xml)
	private Integer idAtendimento;
	private Integer idPrescricaoMedica;
	private Integer idSolicitacaoConsultoria;
	private Boolean escMdbSubstituirConsultoria;
	private PrescricaoMedicaVO prescricaoMedicaVO;
	
	private int idConversacaoAnterior;

	// Pojo sendo editado
	private MpmSolicitacaoConsultoria solicitacaoConsultoria;

	// Variaveis de trabalho para controle da interface
	private boolean possuiOutraSolicitacaoParaEspecialidade = false;

	private boolean consultoriaPossuiResposta = false;

	private String dataSolicitacaoAnterior;

	private MpmPrescricaoMedica prescricaoMedica;
	private List<MpmSolicitacaoConsultoria> listaConsultorias = new ArrayList<MpmSolicitacaoConsultoria>();
	private Map<MpmSolicitacaoConsultoria, Boolean> consultoriasSelecionadas = new HashMap<MpmSolicitacaoConsultoria, Boolean>();

	private boolean confirmaVoltar;
	
	private boolean formChanged;
	
	private boolean confirmaEditarRequired;
	
	private boolean limparPagina;

	
	private MpmSolicitacaoConsultoria consultoria2Edit;

	@PostConstruct
	protected void inicializar(){
		this.begin(conversation);
	}
	
	/**
	 * Faz a inicializacao inicial da tela. Ele avalia os parâmetros passados no
	 * request e inicia uma nova model ou carrega a model com o id LOG.informado
	 * para edição.
	 * 
	 * Configuracao atraves de action no page.xml.
	 * 
	 * ATENÇÃO: Este metodo sera chamado a cada requesicao para esta pagina.
	 */
	public void inicio() {
	 

		reset();
		
		if(this.limparPagina){
			solicitacaoConsultoria = null;
			limparPagina = false;
		}
		
		// Se o pojo for null significa que eh a primeira vez que esta entrando
		// na tela
		// Neste caso fazer a inicializacao do pojo com os parametros do
		// request. Se for
		// o caso usar os servicos da camada de negocio.
		if (solicitacaoConsultoria == null) {
			LOG.info("ID Atendimento: " + idAtendimento);
			LOG.info("ID Prescrição Médica: " + idPrescricaoMedica);
			LOG.info("ID Solicitação de consultoria: " + idSolicitacaoConsultoria);

			prescricaoMedica = prescricaoMedicaFacade.obterPrescricaoPorId(
					idAtendimento, idPrescricaoMedica);

			if (this.idSolicitacaoConsultoria == null) {
				solicitacaoConsultoria = new MpmSolicitacaoConsultoria();
				solicitacaoConsultoria
						.setTipo(DominioTipoSolicitacaoConsultoria.C);
			} else {
				solicitacaoConsultoria = this.prescricaoMedicaFacade
						.obterSolicitacaoConsultoriaPorId(idAtendimento,
								idSolicitacaoConsultoria);
				//controle caso o item tenha sido excluído por outro usuário
				if(solicitacaoConsultoria == null){
					apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
					solicitacaoConsultoria = new MpmSolicitacaoConsultoria();
					solicitacaoConsultoria.setTipo(DominioTipoSolicitacaoConsultoria.C);
					return;
				}				
				if (solicitacaoConsultoria.getDthrResposta() != null) {
					apresentarMsgNegocio(Severity.INFO,
									"MENSAGEM_SOLICITACAO_JA_RESPONDIDA");

					consultoriaPossuiResposta = true;
				}
			}

			this.pesquisarConsultorias();

		}

	
	}

	/**
	 * Método que inicia uma nova solicitação de consultoria
	 */
	public void prepararNovaSolicitacaoConsultoria() {
		reset();
		solicitacaoConsultoria = new MpmSolicitacaoConsultoria();
		solicitacaoConsultoria.setTipo(DominioTipoSolicitacaoConsultoria.C);
		pesquisarConsultorias();
	}

	/**
	 * Método que pesquisa as consultorias de uma prescrição
	 */
	public void pesquisarConsultorias() {
		listaConsultorias.clear();
		consultoriasSelecionadas.clear();
		
		
		listaConsultorias = prescricaoMedicaFacade
			.pesquisarConsultoriasPorPrescricao(prescricaoMedica);
		Collections.sort(listaConsultorias);
		

		for (MpmSolicitacaoConsultoria consultoria : listaConsultorias) {
			consultoriasSelecionadas.put(consultoria, false);
		}
	}

	/**
	 * Muda para a consultoria selecionada no grid
	 * 
	 * @param consultoria
	 */
	public void editarSolicitacaoConsultoria(
			MpmSolicitacaoConsultoria consultoria) {
		if (this.isFormChanged()) {
			this.setConfirmaEditarRequired(true);
			consultoria2Edit = consultoria;
			return;
		}
		
		solicitacaoConsultoria = consultoria;
		if (solicitacaoConsultoria.getDthrResposta() != null) {

			apresentarMsgNegocio(Severity.INFO,
					"MENSAGEM_SOLICITACAO_JA_RESPONDIDA");

			consultoriaPossuiResposta = true;

		}
	}
	
	/**
	 * Confirma edição de outra consultoria.
	 */
	public void confirmaEditarConsultoria() {
		reset();
		editarSolicitacaoConsultoria(consultoria2Edit);
		consultoria2Edit = null;
	}

	/**
	 * Reseta flags para evitar exibição de modais.
	 */
	private void reset() {
		setFormChanged(false);
		setConfirmaEditarRequired(false);
		setConfirmaVoltar(false);
	}

	/**
	 * Método que remove as consultorias selecionadas no grid.
	 */
	public void removerConsultoriasSelecionadas() {

		try {
			int nroConsultoriasRemovidas = 0;
			for (MpmSolicitacaoConsultoria consultoria : listaConsultorias) {
				if (consultoriasSelecionadas.get(consultoria) == true) {
					prescricaoMedicaFacade.excluirSolicitacaoConsultoria(consultoria);
					nroConsultoriasRemovidas++;
				}
			}
			if (nroConsultoriasRemovidas > 0) {
				if (nroConsultoriasRemovidas > 1) {
					apresentarMsgNegocio(
							Severity.INFO,
							"MENSAGEM_SUCESSO_REMOCAO_CONSULTORIAS");
				} else {
					apresentarMsgNegocio(
							Severity.INFO,
							"MENSAGEM_SUCESSO_REMOCAO_CONSULTORIA");
				}
			} else {
				apresentarMsgNegocio(Severity.ERROR,
						"MENSAGEM_NENHUMA_CONSULTORIA_SELECIONADA_REMOCAO");
			}
			// Limpa a tela
			solicitacaoConsultoria = new MpmSolicitacaoConsultoria();
			solicitacaoConsultoria.setTipo(DominioTipoSolicitacaoConsultoria.C);
			// reinicializa o Map de consultorias selecionadas e a listagem de
			// consultorias
			consultoriasSelecionadas = new HashMap<MpmSolicitacaoConsultoria, Boolean>();
			this.pesquisarConsultorias();
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	/**
	 * Método que verifica se o lápis deve ser renderizado nas linhas da tabela
	 * de consultorias
	 * 
	 * @param consultoria
	 * @return
	 */
	public boolean verificarLapisDeveAparecer(
			MpmSolicitacaoConsultoria consultoria) {
		boolean retorno = true;
		if (solicitacaoConsultoria!=null && solicitacaoConsultoria.getId() != null
				&& solicitacaoConsultoria.getId().getSeq().equals(consultoria
						.getId().getSeq())) {
			retorno = false;
		}

		return retorno;
	}

	/**
	 * Método que obtem o estilo da coluna das consultorias da tabela
	 * 
	 * @param consultoria
	 * @return
	 */
	public String obterEstiloColuna(MpmSolicitacaoConsultoria consultoria) {
		String retorno = "";
		if (consultoriasSelecionadas.get(consultoria) != null
				&& consultoriasSelecionadas.get(consultoria)) {
			retorno = "background-color:#FF6347";
		}
		return retorno;

	}

	/**
	 * ATENÇÃO: Houve uma alteração importante relativa a chamada deste metodo
	 * por questoes de performance. Esse metodo eh chamado no action lister no
	 * ajax suppport quando uma especialidade eh selecionada. Ele nao deve ficar
	 * direto na checagem do isRendered porque essa checagem pode ser feita mais
	 * de uma vez ao longo do ciclo de vida. O que poderia acarretar em chamadas
	 * desnecessarias.
	 * 
	 * @throws ApplicationBusinessException
	 */
	public void verificarConsultoriaExistente() throws ApplicationBusinessException {
		if (this.solicitacaoConsultoria.getEspecialidade() != null) {
			verificarConsultoriaExistente(this.solicitacaoConsultoria
					.getEspecialidade());
		}
		setFormChanged(true);
	}

	public void verificarConsultoriaExistente(AghEspecialidades especialidade)
			throws ApplicationBusinessException {

		this.solicitacaoConsultoria.setEspecialidade(especialidade);

		AghParametros param = parametroFacade
				.buscarAghParametro(AghuParametrosEnum.P_AGHU_MODULO_RESPOSTA_CONSULTORIA_IMPLANTADO);

		boolean validarEspecialidadeDuplicada = param.getVlrTexto() == null ? false
				: Boolean.valueOf(param.getVlrTexto());

		if (validarEspecialidadeDuplicada && especialidade != null && this.solicitacaoConsultoria.getId() == null
				&& !possuiOutraSolicitacaoParaEspecialidade) {
			MpmSolicitacaoConsultoria solicitacaoAnterior = this.prescricaoMedicaFacade
					.verificarSolicitacaoConsultoriaAtivaPorEspecialidade(especialidade.getSeq(), this.idAtendimento,
							this.idPrescricaoMedica);
			if (solicitacaoAnterior == null) {
				possuiOutraSolicitacaoParaEspecialidade = false;
			} else {
				possuiOutraSolicitacaoParaEspecialidade = true;
				SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
				dataSolicitacaoAnterior = df.format(solicitacaoAnterior.getCriadoEm());
				
				if(this.solicitacaoConsultoria.getEspecialidade() != null){
					openDialog("modalpanelWG");
				}
			}
		}
	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de solicitação de
	 * consultoria
	 */
	public void confirmar() {
		this.confirmaVoltar = false;
		LOG.debug(solicitacaoConsultoria);

		try {

			boolean isInclusao = false;

			if (solicitacaoConsultoria.getId() == null) {
				isInclusao = true;

			}
			this.prescricaoMedicaFacade.persistirSolicitacaoConsultoria(
					solicitacaoConsultoria, idAtendimento,
					idPrescricaoMedica);

			this.pesquisarConsultorias();
			this.prepararNovaSolicitacaoConsultoria();

			// inclusão
			if (isInclusao) {
				apresentarMsgNegocio(Severity.INFO, "SUCESSO_INCLUSAO_CONSULTORIA");

				// edição
			} else {
				apresentarMsgNegocio(Severity.INFO, "SUCESSO_EDICAO_CONSULTORIA");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		
	}

	public boolean isReadOnly() {
		return this.solicitacaoConsultoria!= null && this.solicitacaoConsultoria.getId() != null
				&& this.solicitacaoConsultoria.getEspecialidade() != null;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de solicitação de
	 * consultoria
	 */
	public String cancelar() {

		this.limparFlagExibicaoModal();
		LOG.info("Cancelado");

		if (this.isFormChanged()) {
			this.confirmaVoltar = true;
			return null;
		}

		return voltar();
	}

	public String voltar() {
		return PAGINA_MANTER_PRESCRICAO_MEDICA;
	}

	public List<AghEspecialidades> pesquisarPermitemConsultoriaPorSigla(String param) {
		return aghuFacade.listarPermitemConsultoriaPorSigla(param);
	}

	// Getters and Setters - Melhor deixar eles por último e isolados mesmo.
	// Outra forma é ao invés de usar get e set colocar variáveis outjecteds.
	public Integer getIdAtendimento() {
		return idAtendimento;
	}

	public void setIdAtendimento(Integer idAtendimento) {
		this.idAtendimento = idAtendimento;
	}

	public Integer getIdPrescricaoMedica() {
		return idPrescricaoMedica;
	}

	public void setIdPrescricaoMedica(Integer idPrescricaoMedica) {
		this.idPrescricaoMedica = idPrescricaoMedica;
	}

	public MpmSolicitacaoConsultoria getSolicitacaoConsultoria() {
		return solicitacaoConsultoria;
	}

	public void setSolicitacaoConsultoria(
			MpmSolicitacaoConsultoria solicitacaoConsultoria) {
		this.solicitacaoConsultoria = solicitacaoConsultoria;
	}

	public Boolean getEscMdbSubstituirConsultoria() {
		return escMdbSubstituirConsultoria;
	}

	public void setEscMdbSubstituirConsultoria(
			Boolean escMdbSubstituirConsultoria) {
		this.escMdbSubstituirConsultoria = escMdbSubstituirConsultoria;
	}

	public boolean isPossuiOutraSolicitacaoParaEspecialidade() {
		// TODO Remover LOG.info. Importante para entender a importancia de usar
		// esse metodo desta forma.
		LOG.info("Checando ja existe solicitacao: "
				+ possuiOutraSolicitacaoParaEspecialidade);
		return possuiOutraSolicitacaoParaEspecialidade;
	}

	public void setPossuiOutraSolicitacaoParaEspecialidade(
			boolean possuiOutraSolicitacaoParaEspecialidade) {
		this.possuiOutraSolicitacaoParaEspecialidade = possuiOutraSolicitacaoParaEspecialidade;
	}

	public Integer getIdSolicitacaoConsultoria() {
		return idSolicitacaoConsultoria;
	}

	public void setIdSolicitacaoConsultoria(Integer idSolicitacaoConsultoria) {
		this.idSolicitacaoConsultoria = idSolicitacaoConsultoria;
	}

	public boolean isConsultoriaPossuiResposta() {
		return consultoriaPossuiResposta;
	}

	public void setConsultoriaPossuiResposta(boolean consultoriaPossuiResposta) {
		this.consultoriaPossuiResposta = consultoriaPossuiResposta;
	}

	public void limparFlagExibicaoModal() {
		this.possuiOutraSolicitacaoParaEspecialidade = false;
	}

	public MpmPrescricaoMedica getPrescricaoMedica() {
		return prescricaoMedica;
	}

	public void setPrescricaoMedica(MpmPrescricaoMedica prescricaoMedica) {
		this.prescricaoMedica = prescricaoMedica;
	}

	public List<MpmSolicitacaoConsultoria> getListaConsultorias() {
		return listaConsultorias;
	}

	public void setListaConsultorias(
			List<MpmSolicitacaoConsultoria> listaConsultorias) {
		this.listaConsultorias = listaConsultorias;
	}

	public Map<MpmSolicitacaoConsultoria, Boolean> getConsultoriasSelecionadas() {
		return consultoriasSelecionadas;
	}

	public void setConsultoriasSelecionadas(
			Map<MpmSolicitacaoConsultoria, Boolean> consultoriasSelecionadas) {
		this.consultoriasSelecionadas = consultoriasSelecionadas;
	}

	public boolean isConfirmaVoltar() {
		return confirmaVoltar;
	}

	public void setConfirmaVoltar(boolean confirmaVoltar) {
		this.confirmaVoltar = confirmaVoltar;
	}

	public int getIdConversacaoAnterior() {
		return idConversacaoAnterior;
	}

	public void setIdConversacaoAnterior(int idConversacaoAnterior) {
		this.idConversacaoAnterior = idConversacaoAnterior;
	}

	public boolean isFormChanged() {
		return formChanged;
	}

	public void setFormChanged(boolean formChanged) {
		this.formChanged = formChanged;
	}

	public boolean isConfirmaEditarRequired() {
		return confirmaEditarRequired;
	}

	public void setConfirmaEditarRequired(boolean confirmaEditarRequired) {
		this.confirmaEditarRequired = confirmaEditarRequired;
	}
	
	public String getDataSolicitacaoAnterior() {
		return dataSolicitacaoAnterior;
	}

	public void setDataSolicitacaoAnterior(String dataSolicitacaoAnterior) {
		this.dataSolicitacaoAnterior = dataSolicitacaoAnterior;
	}

	public PrescricaoMedicaVO getPrescricaoMedicaVO() {
		return prescricaoMedicaVO;
	}

	public void setPrescricaoMedicaVO(PrescricaoMedicaVO prescricaoMedicaVO) {
		this.prescricaoMedicaVO = prescricaoMedicaVO;
	}
	
	public boolean isLimparPagina() {
		return limparPagina;
	}

	public void setLimparPagina(boolean limparPagina) {
		this.limparPagina = limparPagina;
	}
}
