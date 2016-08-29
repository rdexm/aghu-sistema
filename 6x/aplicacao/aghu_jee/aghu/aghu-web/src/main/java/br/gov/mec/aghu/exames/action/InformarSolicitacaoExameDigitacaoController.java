package br.gov.mec.aghu.exames.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.action.IdentificarUnidadeExecutoraController;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.AelItemSolicitacaoExamesVO;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AelUnidExecUsuario;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class InformarSolicitacaoExameDigitacaoController extends ActionController {

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	private static final long serialVersionUID = -445357254089981119L;

	private static final String PAGE_EXAMES_INFORMAR_SOLICITACAO_EXAME_DIGITACAO = "exames-informarSolicitacaoExameDigitacao";
	private static final String PAGE_EXAMES_RESULTADO_NOTA_ADICIONAL = "exames-resultadoNotaAdicional";

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@Inject
	private IdentificarUnidadeExecutoraController unidadeExecutoraController;

	@EJB
	private IExamesFacade examesFacade;

	@EJB
	private IPermissionService permissionService;
	
	@Inject
	private CadastroResultadosNotaAdicionalController cadastroResultadosNotaAdicionalController;

	// Campos de filtro para pesquisa
	private AghUnidadesFuncionais unidadeExecutora;
	private Integer solicitacaoSeq;
	private Short amostraSeqp;

	// Instancias para resultados da pesquisa
	private AelUnidExecUsuario usuarioUnidadeExecutora;
	private List<AelItemSolicitacaoExames> listaItensSolicExames;
	// private List<AelItemSolicConsultadoVO> listaItemSolicConsultado;
	private String nomePasciente;
	private String unidadeSoliciante;
	private String nomeSolicitante;

	// Instancias pesquisa de servidores responsaveis pela liberacao
	private RapServidores servidor;
	private Integer matricula;
	private Short vinCodigo;
	private String servidorDesc;
	private List<AelItemSolicitacaoExamesVO> listaMascarasAtivasExame;
	private Integer soeSeq;
	private Short seqp;
	private Integer velSeqp;

	private Short unfSeq;
	private String voltarPara;

	private Boolean erroAnular = Boolean.FALSE;

	/**
	 * Chamado no inicio de cada conversacao
	 */
	public void iniciar() {
	 


		if (this.unfSeq != null) {
			// Quando a unidade é informada via parâmetro
			this.unidadeExecutora = this.aghuFacade.obterUnidadeFuncional(this.unfSeq);
		} else {

			// Obtem o USUARIO da unidade executora
			try {
				this.usuarioUnidadeExecutora = this.examesFacade.obterUnidExecUsuarioPeloId(registroColaboradorFacade.obterServidorAtivoPorUsuario(this.obterLoginUsuarioLogado()).getId());
			} catch (ApplicationBusinessException e) {
				this.usuarioUnidadeExecutora = null;
			}

			// Resgata a unidade executora associada ao usuario
			if (this.usuarioUnidadeExecutora != null) {
				this.unidadeExecutora = this.usuarioUnidadeExecutora.getUnfSeq();
			}
		}

		// Reseta todos os filtros
		if (solicitacaoSeq != null) {
			pesquisar();
		}

		// if(this.erroAnular != null && this.erroAnular){
		// this.executarRefreshLista();
		// }

	}

	// public void executarRefreshLista() {
	// if(this.listaItensSolicExames != null && !this.listaItensSolicExames.isEmpty()) {
	// for (AelItemSolicitacaoExames entity : this.listaItensSolicExames) {
	// this.examesFacade.refresh(entity);
	// }
	// }
	// }

	/**
	 * Persiste identificacao da unidade executora atraves do usuario logado
	 */
	public void persistirIdentificacaoUnidadeExecutora() {
		try {

			// Persiste identificacao da unidade executora do usuario
			this.cadastrosApoioExamesFacade.persistirIdentificacaoUnidadeExecutora(this.usuarioUnidadeExecutora, this.unidadeExecutora);
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
	}

	/**
	 * Pesquisa solicitacoes de exame e seus respectivos itens
	 */
	public void pesquisar() {

		this.listaItensSolicExames = null;

		if (this.unidadeExecutora == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_OBRIGATORIO_UNIDADE_EXECUTORA_ARQUIVO_LAUDO");
			return;
		}

		if (this.solicitacaoSeq == null) {
			this.apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_CAMPO_OBRIGATORIO_SOLICITACAO_ARQUIVO_LAUDO");
			return;
		}

		// Resgata a solicitacao de exame através da id
		AelSolicitacaoExames solicitacaoExame = this.examesFacade.obterAelSolicitacaoExamesPeloId(this.solicitacaoSeq);

		// Caso a solicitacao de exame exista realiza a consulta de itens de solicitacao de exame
		if (solicitacaoExame != null) {
			try {

				// Resgata nome do paciente atraves da solicitacao de exame
				if (solicitacaoExame.getAtendimento() != null) {
					this.nomePasciente = solicitacaoExame.getAtendimento().getPaciente().getNome();
				}

				// Resgata o nome da unidade solicitante atraves da view VAghUnidFuncional
				this.unidadeSoliciante = this.examesFacade.obterDescricaoVAghUnidFuncional(solicitacaoExame.getUnidadeFuncional().getSeq());

				// Resgata nome do solicitante atraves da solicitacao de exame
				if (solicitacaoExame.getServidorResponsabilidade() != null) {
					this.nomeSolicitante = solicitacaoExame.getServidorResponsabilidade().getPessoaFisica().getNome();
				}

				// Pesquisa itens de solicitacao de exame
				this.listaItensSolicExames = this.examesFacade.pesquisarInformarSolicitacaoExameDigitacaoController(this.solicitacaoSeq,
						(this.amostraSeqp != null ? this.amostraSeqp.intValue() : null), this.unidadeExecutora.getSeq());

				if (this.listaItensSolicExames == null || this.listaItensSolicExames.isEmpty()) {
					this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUM_ITEM_SOLICITACAO_EXAME_ARQUIVO_LAUDO");
				}

			} catch (BaseException e) {
				super.apresentarExcecaoNegocio(e);
			}
		} else {
			this.apresentarMsgNegocio(Severity.WARN, "MENSAGEM_NENHUMA_SOLICITACAO_EXAME_ARQUIVO_LAUDO");
		}
	}

	/*
	 * Metodos para Suggestion Box
	 */

	/**
	 * Metodo para pesquisa na suggestion box de unidade executora
	 */
	public List<AghUnidadesFuncionais> obterAghUnidadesFuncionaisExecutoras(String objPesquisa) {
		return this.aghuFacade.pesquisarUnidadesExecutorasPorCodigoOuDescricao(objPesquisa);
	}

	/**
	 * Limpa os filtros da consulta
	 */
	public void limparPesquisa() {

		this.listaItensSolicExames = null;

		// Resgata a unidade executora associada ao usuario
		this.unidadeExecutora = this.unidadeExecutoraController.getUnidadeExecutora();

		this.amostraSeqp = null;
		this.solicitacaoSeq = null;
		this.nomePasciente = null;
		this.unidadeSoliciante = null;
		this.nomeSolicitante = null;
		this.servidor = null;
		this.servidorDesc = null;
		this.matricula = null;
		this.vinCodigo = null;

	}

	/**
	 * Método utilizado para verificar <br>
	 * se há mais de uma máscara associada <br>
	 * a um exame. Se sim, deve exibir uma modal<br>
	 * com a lista de máscaras a serem selecionadas.
	 * 
	 * @param item
	 * @return
	 */
	// public String verificarMascarasCadastradas(AelItemSolicitacaoExames item) {
	// this.listaMascarasAtivasExame = this.examesFacade.listarMascarasAtivasPorExame(item.getAelExameMaterialAnalise().getId().getExaSigla(),
	// item.getAelExameMaterialAnalise().getId().getManSeq(),
	// item.getId().getSoeSeq(), item.getId().getSeqp());
	//
	// if (!this.listaMascarasAtivasExame.isEmpty() && this.listaMascarasAtivasExame.size() > 1) {
	// this.exibirModal = Boolean.TRUE;
	// return null;
	// } else {
	// if (this.listaMascarasAtivasExame.isEmpty()) {
	// this.soeSeq = item.getId().getSoeSeq();
	// this.seqp = item.getId().getSeqp();
	// } else {
	// final AelItemSolicitacaoExamesVO itemVO = this.listaMascarasAtivasExame.get(0);
	// this.soeSeq = itemVO.getIseSoeSeq();
	// this.seqp = itemVO.getIseSeqp();
	// this.velSeqp = itemVO.getVelSeqp();
	// }
	//
	// this.cadastroResultadosNotaAdicionalController.setVoltarPara(PAGE_EXAMES_INFORMAR_SOLICITACAO_EXAME_DIGITACAO);
	// this.cadastroResultadosNotaAdicionalController.setSolicitacaoExameSeq(this.soeSeq);
	// this.cadastroResultadosNotaAdicionalController.setItemSolicitacaoExameSeq(this.seqp);
	// this.cadastroResultadosNotaAdicionalController.setVelSeqp(this.velSeqp);
	//
	// return PAGE_EXAMES_RESULTADO_NOTA_ADICIONAL;
	// }
	// }

	/**
	 * Método utilizado para verificar <br>
	 * se há mais de uma máscara associada <br>
	 * a um exame. Se sim, deve exibir uma modal<br>
	 * com a lista de máscaras a serem selecionadas.
	 * 
	 * @param item
	 * @return
	 */
	public String verificarMascarasCadastradasNew(AelItemSolicitacaoExames item) {

		this.listaMascarasAtivasExame = this.examesFacade.listarMascarasAtivasPorExame(item.getAelExameMaterialAnalise().getId().getExaSigla(), item.getAelExameMaterialAnalise().getId().getManSeq(),
				item.getId().getSoeSeq(), item.getId().getSeqp());

		if (!this.listaMascarasAtivasExame.isEmpty() && this.listaMascarasAtivasExame.size() > 1) {
			openDialog("modalDefinirMascaraWG");
			return null;
		} else {
			return PAGE_EXAMES_RESULTADO_NOTA_ADICIONAL;
		}
	}
	
	
	/**
	 * Método utilizado para verificar <br>
	 * se há mais de uma máscara associada <br>
	 * a um exame. Se sim, deve exibir uma modal<br>
	 * com a lista de máscaras a serem selecionadas.
	 * 
	 * @param item
	 * @return
	 */
	public String verificarMascarasCadastradas(AelItemSolicitacaoExames item) {
		
		// TODO REMOVER DEBUG
//		<rule if-outcome="manterResultadoNotaAdicional">
//		<begin-conversation nested="true"/>
//		<redirect view-id="/exames/cadastroResultadosNotaAdicional.xhtml">
//			<param name="voltarPara" value="informarSolicitacaoExameDigitacao"/>
//			<param name="soeSeq" value="#{informarSolicitacaoExameDigitacaoController.soeSeq}"/>
//			<param name="seqp" value="#{informarSolicitacaoExameDigitacaoController.seqp}"/>
//			<param name="velSeqp" value="#{informarSolicitacaoExameDigitacaoController.velSeqp}"/>
//		</redirect>
//	</rule>	
		
		
		// TODO REMOVER DEBUG
//		<param name="soeSeq" required="false" value="#{cadastroResultadosNotaAdicionalController.solicitacaoExameSeq}" />
//		<param name="seqp" required="false" value="#{cadastroResultadosNotaAdicionalController.itemSolicitacaoExameSeq}" />
//		<param name="velSeqp" required="false" value="#{cadastroResultadosNotaAdicionalController.velSeqp}" />
//		<param name="currentTabIndex" required="false" value="#{cadastroResultadosNotaAdicionalController.currentTabIndex}" />
//		<param name="voltarPara" required="false" value="#{cadastroResultadosNotaAdicionalController.voltarPara}" />
//		<param name="resultPadSeq" required="false" value="#{cadastroResultadosNotaAdicionalController.resultPadSeq}" />
		
		
		this.listaMascarasAtivasExame = this.examesFacade.listarMascarasAtivasPorExame(
					item.getAelExameMaterialAnalise().getId().getExaSigla(), 
					item.getAelExameMaterialAnalise().getId().getManSeq(),
					item.getId().getSoeSeq(), item.getId().getSeqp());
		
		if(!this.listaMascarasAtivasExame.isEmpty()	&& this.listaMascarasAtivasExame.size() > 1) {
			openDialog("modalDefinirMascaraWG");
			return null;
		}else {
			if(this.listaMascarasAtivasExame.isEmpty()) {
				this.soeSeq = item.getId().getSoeSeq();
				this.seqp = item.getId().getSeqp();
			}else {
				final AelItemSolicitacaoExamesVO itemVO = this.listaMascarasAtivasExame.get(0);
				this.soeSeq = itemVO.getIseSoeSeq();
				this.seqp = itemVO.getIseSeqp();
				this.velSeqp = itemVO.getVelSeqp();
			}
			
			this.cadastroResultadosNotaAdicionalController.setVoltarPara(PAGE_EXAMES_INFORMAR_SOLICITACAO_EXAME_DIGITACAO);
			this.cadastroResultadosNotaAdicionalController.setSolicitacaoExameSeq(this.soeSeq);
			this.cadastroResultadosNotaAdicionalController.setItemSolicitacaoExameSeq(this.seqp);
			this.cadastroResultadosNotaAdicionalController.setVelSeqp(this.velSeqp);
			
			return PAGE_EXAMES_RESULTADO_NOTA_ADICIONAL;
		}
	}
	
	public String selecionarMascara() {
		return PAGE_EXAMES_RESULTADO_NOTA_ADICIONAL;
	}
	
	/**
	 * Método utilizado para verificar <br>
	 * se há mais de uma máscara associada <br>
	 * a um exame. Se sim, deve exibir uma modal<br>
	 * com a lista de máscaras a serem selecionadas.
	 * 
	 * @param item
	 * @return
	 */
	public String verificarMascarasCadastradasOLD(AelItemSolicitacaoExames item) {
		this.listaMascarasAtivasExame = this.examesFacade
			.listarMascarasAtivasPorExame(
					item.getAelExameMaterialAnalise().getId().getExaSigla(), 
					item.getAelExameMaterialAnalise().getId().getManSeq(),
					item.getId().getSoeSeq(), item.getId().getSeqp());
		
		if(!this.listaMascarasAtivasExame.isEmpty()
				&& this.listaMascarasAtivasExame.size() > 1) {
			openDialog("modalDefinirMascaraWG");
			return null;
		}else {
			if(this.listaMascarasAtivasExame.isEmpty()) {
				this.soeSeq = item.getId().getSoeSeq();
				this.seqp = item.getId().getSeqp();
			}else {
				final AelItemSolicitacaoExamesVO itemVO = this.listaMascarasAtivasExame.get(0);
				this.soeSeq = itemVO.getIseSoeSeq();
				this.seqp = itemVO.getIseSeqp();
				this.velSeqp = itemVO.getVelSeqp();
			}
			
			return "manterResultadoNotaAdicional";
		}
	}

	public void verificarMascarasCadastradas() {

		if (PAGE_EXAMES_INFORMAR_SOLICITACAO_EXAME_DIGITACAO.equalsIgnoreCase(this.voltarPara)) {

			AelItemSolicitacaoExames item = null; // TODO LOCAL

			this.listaMascarasAtivasExame = this.examesFacade.listarMascarasAtivasPorExame(item.getAelExameMaterialAnalise().getId().getExaSigla(), item.getAelExameMaterialAnalise().getId()
					.getManSeq(), item.getId().getSoeSeq(), item.getId().getSeqp());

			if (this.listaMascarasAtivasExame.isEmpty()) {
				this.soeSeq = item.getId().getSoeSeq();
				this.seqp = item.getId().getSeqp();
			} else {
				final AelItemSolicitacaoExamesVO itemVO = this.listaMascarasAtivasExame.get(0);
				this.soeSeq = itemVO.getIseSoeSeq();
				this.seqp = itemVO.getIseSeqp();
				this.velSeqp = itemVO.getVelSeqp();
			}

			// this.cadastroResultadosNotaAdicionalController.setVoltarPara(PAGE_EXAMES_INFORMAR_SOLICITACAO_EXAME_DIGITACAO);
			// this.cadastroResultadosNotaAdicionalController.setSolicitacaoExameSeq(this.soeSeq);
			// this.cadastroResultadosNotaAdicionalController.setItemSolicitacaoExameSeq(this.seqp);
			// this.cadastroResultadosNotaAdicionalController.setVelSeqp(this.velSeqp);
		}

	}

	/**
	 * Verifica se tem permissão <br>
	 * para mostrar o link de editar na tela.
	 * 
	 * @return
	 */
	public Boolean verificarPermissoes() {

		if (this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "preencherLaudoExames", "executar")
				| this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "consultarNotaAdicionalResultExame", "executar")
				|| this.permissionService.usuarioTemPermissao(this.obterLoginUsuarioLogado(), "informarSolicitacaoExameDigitacao", "executar")) {
			return Boolean.FALSE;
		}
		return Boolean.TRUE;
	}

	/**
	 * Ação do botão voltar
	 * 
	 * @return
	 */
	public String voltar() {

		final String retorno = this.voltarPara;

		this.unidadeExecutora = null;
		this.solicitacaoSeq = null;
		this.amostraSeqp = null;

		this.usuarioUnidadeExecutora = null;
		this.listaItensSolicExames = null;
		this.nomePasciente = null;
		this.unidadeSoliciante = null;
		this.nomeSolicitante = null;

		this.servidor = null;
		this.matricula = null;
		this.vinCodigo = null;
		this.servidorDesc = null;
		this.listaMascarasAtivasExame = null;
		this.soeSeq = null;
		this.seqp = null;
		this.velSeqp = null;

		this.unfSeq = null;
		this.voltarPara = null;

		this.erroAnular = Boolean.FALSE;

		return retorno;
	}

	/*
	 * Getters e Setters
	 */

	public AghUnidadesFuncionais getUnidadeExecutora() {
		return unidadeExecutora;
	}

	public void setUnidadeExecutora(AghUnidadesFuncionais unidadeExecutora) {
		this.unidadeExecutora = unidadeExecutora;
	}

	public Integer getSolicitacaoSeq() {
		return solicitacaoSeq;
	}

	public void setSolicitacaoSeq(Integer solicitacaoSeq) {
		this.solicitacaoSeq = solicitacaoSeq;
	}

	public Short getAmostraSeqp() {
		return amostraSeqp;
	}

	public void setAmostraSeqp(Short amostraSeqp) {
		this.amostraSeqp = amostraSeqp;
	}

	public String getNomePasciente() {
		return nomePasciente;
	}

	public void setNomePasciente(String nomePasciente) {
		this.nomePasciente = nomePasciente;
	}

	public String getUnidadeSoliciante() {
		return unidadeSoliciante;
	}

	public void setUnidadeSoliciante(String unidadeSoliciante) {
		this.unidadeSoliciante = unidadeSoliciante;
	}

	public String getNomeSolicitante() {
		return nomeSolicitante;
	}

	public void setNomeSolicitante(String nomeSolicitante) {
		this.nomeSolicitante = nomeSolicitante;
	}

	public AelUnidExecUsuario getUsuarioUnidadeExecutora() {
		return usuarioUnidadeExecutora;
	}

	public void setUnidadeExecutoraController(IdentificarUnidadeExecutoraController unidadeExecutoraController) {
		this.unidadeExecutoraController = unidadeExecutoraController;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public Integer getMatricula() {
		return matricula;
	}

	public void setMatricula(Integer matricula) {
		this.matricula = matricula;
	}

	public Short getVinCodigo() {
		return vinCodigo;
	}

	public void setVinCodigo(Short vinCodigo) {
		this.vinCodigo = vinCodigo;
	}

	public String getServidorDesc() {
		return servidorDesc;
	}

	public void setServidorDesc(String servidorDesc) {
		this.servidorDesc = servidorDesc;
	}

	public List<AelItemSolicitacaoExames> getListaItensSolicExames() {
		return listaItensSolicExames;
	}

	public void setListaItensSolicExames(List<AelItemSolicitacaoExames> listaItensSolicExames) {
		this.listaItensSolicExames = listaItensSolicExames;
	}

	public List<AelItemSolicitacaoExamesVO> getListaMascarasAtivasExame() {
		return listaMascarasAtivasExame;
	}

	public void setListaMascarasAtivasExame(List<AelItemSolicitacaoExamesVO> listaMascarasAtivasExame) {
		this.listaMascarasAtivasExame = listaMascarasAtivasExame;
	}

	public Integer getSoeSeq() {
		return soeSeq;
	}

	public void setSoeSeq(Integer soeSeq) {
		this.soeSeq = soeSeq;
	}

	public Short getSeqp() {
		return seqp;
	}

	public void setSeqp(Short seqp) {
		this.seqp = seqp;
	}

	public Integer getVelSeqp() {
		return velSeqp;
	}

	public void setVelSeqp(Integer velSeqp) {
		this.velSeqp = velSeqp;
	}

	public String getVoltarPara() {

		return voltarPara;
	}

	public void setVoltarPara(String voltarPara) {
		this.voltarPara = voltarPara;
	}

	public Short getUnfSeq() {
		return unfSeq;
	}

	public void setUnfSeq(Short unfSeq) {
		this.unfSeq = unfSeq;
	}

	public Boolean getErroAnular() {
		return erroAnular;
	}

	public void setErroAnular(Boolean erroAnular) {
		this.erroAnular = erroAnular;
	}

}
