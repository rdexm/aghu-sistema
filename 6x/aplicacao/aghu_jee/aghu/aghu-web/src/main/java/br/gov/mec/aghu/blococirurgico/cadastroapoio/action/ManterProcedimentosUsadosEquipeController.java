package br.gov.mec.aghu.blococirurgico.cadastroapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.blococirurgico.cadastroapoio.business.IBlocoCirurgicoCadastroApoioFacade;
import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.MbcProcPorEquipe;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterProcedimentosUsadosEquipeController extends ActionController {

	@PostConstruct
	protected void inicializar(){
	 this.begin(conversation);
	}

	private static final String TOP_N_PROCS_LIST = "pesquisaProcedimentosUsadosEquipe";
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 4621191686694271660L;

	/*
	 * Injeções
	 */

	@EJB
	private IAghuFacade aghuFacade;

	@EJB
	private IBlocoCirurgicoCadastroApoioFacade blocoCirurgicoCadastroApoioFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;
	

	/**
	 * Instância que será gravada
	 */
	private MbcProcPorEquipe procPorEquipe;

	/**
	 * Chamado no inicio de cada conversação
	 */
	public void inicio() {
	 

	 

		// Cria nova instância de MbcProcPorEquipe
		this.procPorEquipe = new MbcProcPorEquipe();
	
	}
	

	/**
	 * Gravar
	 * 
	 * @return
	 */
	public String gravar() {
		try {
			this.blocoCirurgicoCadastroApoioFacade.inserirProcedimentoUsadoPorEquipe(this.procPorEquipe);

			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_INSERIR_PROCEDIMENTOS_MAIS_USADOS_EQUIPE");

			return TOP_N_PROCS_LIST;

		} catch (BaseException e) {
			super.apresentarExcecaoNegocio(e);
		}
		return null;
	}

	/**
	 * Obtem unidade funcional ativa executora de cirurgias
	 */
	public List<AghUnidadesFuncionais> obterUnidadeFuncional(String filtro) {
		return this.returnSGWithCount(this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgias(filtro),obterUnidadeFuncionalCount(filtro));
	}
	
	public Long obterUnidadeFuncionalCount(String filtro) {
        return this.aghuFacade.pesquisarUnidadesFuncionaisAtivasUnidadeExecutoraCirurgiasCount(filtro);
    }

	/**
	 * Obtem servidores das equipes
	 */
	public List<RapServidores> obterEquipe(String filtro) {
		try {
			return this.returnSGWithCount(registroColaboradorFacade
				.pesquisarServidorPorSituacaoAtivoParaProcedimentos(filtro, this.obterUnfSeq()),obterEquipeCount(filtro));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return new ArrayList<RapServidores>();
	}

	/**
	 * Obtem quantidade de servidores das equipes
	 * 
	 * @param param
	 * @return
	 * @throws BaseException
	 */
	public Integer obterEquipeCount(String param) throws BaseException {
		try {
			return this.registroColaboradorFacade
				.pesquisarServidorPorSituacaoAtivoParaProcedimentosCount(param, this.obterUnfSeq());
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		}
		return Integer.valueOf(0);
	}

	/**
	 * Obtem procedimentos cirurgicos ativos
	 */
	public List<MbcProcedimentoCirurgicos> obterProcedimento(String filtro) {
		return this.returnSGWithCount(this.blocoCirurgicoCadastroApoioFacade.pesquisarProcedimentosCirurgicosPorCodigoDescricao(filtro, 
				MbcProcedimentoCirurgicos.Fields.DESCRICAO.toString(), 100, DominioSituacao.A),obterProcedimentoCount(filtro));
	}
	
	public Long obterProcedimentoCount(String filtro) {
        return this.blocoCirurgicoCadastroApoioFacade.pesquisarProcedimentosCirurgicosPorCodigoDescricaoCount(filtro, DominioSituacao.A);
    }

	/**
	 * Método chamado para o botão voltar
	 */
	public String cancelar() {
		return TOP_N_PROCS_LIST;
	}
	
	
	private Short obterUnfSeq() {
		if(this.procPorEquipe != null 
				&& this.procPorEquipe.getAghUnidadesFuncionais() != null) {
			return this.procPorEquipe.getAghUnidadesFuncionais().getSeq();
		}
		return null;
	}

	/*
	 * Getters e Setters
	 */

	public MbcProcPorEquipe getProcPorEquipe() {
		return procPorEquipe;
	}
	
	public void setProcPorEquipe(MbcProcPorEquipe procPorEquipe) {
		this.procPorEquipe = procPorEquipe;
	}

}