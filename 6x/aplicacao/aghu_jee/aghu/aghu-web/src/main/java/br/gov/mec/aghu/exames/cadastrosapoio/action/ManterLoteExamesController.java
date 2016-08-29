package br.gov.mec.aghu.exames.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.business.IExamesFacade;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.model.AelExamesMaterialAnalise;
import br.gov.mec.aghu.model.AelGrupoExameUsual;
import br.gov.mec.aghu.model.AelLoteExame;
import br.gov.mec.aghu.model.AelLoteExameId;
import br.gov.mec.aghu.model.AelLoteExameUsual;
import br.gov.mec.aghu.model.AghEspecialidades;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;


public class ManterLoteExamesController extends ActionController {

	private static final long serialVersionUID = 5057026214524554488L;

	private static final String PESQUISA_LOTE_EXAMES = "exames-pesquisaLoteExames";

	@EJB
	private IExamesFacade examesFacade;
	
	@EJB
	private ICadastrosApoioExamesFacade cadastrosApoioExamesFacade;

	@EJB
	private IAghuFacade aghuFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	@EJB
	private ISolicitacaoExameFacade solicitacaoExameFacade;

	private AelLoteExameUsual loteExameUsual = null;
	private Short loteExameSeq;

	private List<AelLoteExame> aelLoteExames = new ArrayList<AelLoteExame>(0);
	private AelExamesMaterialAnalise exameMaterialAnalise;
	private AelLoteExameId loteExameId;
	
	private DominioOrigemAtendimento[] origensPermitidas = {DominioOrigemAtendimento.A, DominioOrigemAtendimento.I,
															DominioOrigemAtendimento.U,	DominioOrigemAtendimento.X,
															DominioOrigemAtendimento.D, DominioOrigemAtendimento.H};

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public String iniciar() {
	 

		setExameMaterialAnalise(null);

		if (this.loteExameSeq != null) {
			this.loteExameUsual = this.examesFacade.getLoteExameUsualPorSeq(this.loteExameSeq);

			if(loteExameUsual == null){
				apresentarMsgNegocio(Severity.ERROR, "OPTIMISTIC_LOCK");
				return voltar();
			}
			
			//alteração referente a issue 11898
			setAelLoteExames(this.examesFacade.pesquisaLotesExamesPorLoteExameUsual(this.loteExameUsual.getSeq()));
		}else{
			this.loteExameUsual = new AelLoteExameUsual();
			loteExameUsual.setIndSituacao(DominioSituacao.A);
			loteExameUsual.setIndLoteDefault(DominioSimNao.N);
		}

		return null;
	
	}

	public void confirmar(){
		try{
//			setIndLoteDefault((getIndLoteDefaultBool())?(DominioSimNao.S):(DominioSimNao.N));
	
			if (this.loteExameUsual.getSeq() == null) {
				this.solicitacaoExameFacade.inserirAelLoteExameUsual(loteExameUsual);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CRIACAO_LOTE_EXAME_USUAL");
				this.loteExameSeq = this.loteExameUsual.getSeq();
				
			} else {

				this.loteExameSeq = this.loteExameUsual.getSeq();
				this.solicitacaoExameFacade.atualizarAelLoteExameUsual(this.loteExameUsual);
				this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EDICAO_LOTE_EXAME_USUAL");
			}

		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
			
		} finally {
			setAelLoteExames(this.examesFacade.pesquisaLotesExamesPorLoteExameUsual(this.loteExameUsual.getSeq()));
		}
	}
	
	public String voltar() {
		loteExameSeq = null;
		loteExameUsual = null;
		return PESQUISA_LOTE_EXAMES;
	}

	public void confirmarExame(){

		try{
			AelLoteExame loteExame = new AelLoteExame();
	
			AelLoteExameId loteExameId = new AelLoteExameId();
			loteExameId.setLeuSeq(this.loteExameUsual.getSeq());
			loteExameId.setEmaExaSigla(exameMaterialAnalise.getAelExames().getSigla());
			loteExameId.setEmaManSeq(exameMaterialAnalise.getAelMateriaisAnalises().getSeq());
			
			loteExame.setAelLoteExamesUsuais(this.loteExameUsual);
			loteExame.setExamesMaterialAnalise(this.exameMaterialAnalise);
			loteExame.setId(loteExameId);

			this.cadastrosApoioExamesFacade.persistirAelLoteExame(loteExame, true);
			this.exameMaterialAnalise = null;
			setAelLoteExames(this.examesFacade.pesquisaLotesExamesPorLoteExameUsual(this.loteExameUsual.getSeq()));
		} catch (BaseException e) {
			apresentarExcecaoNegocio(e);
		} 
	}
	
	public void excluirExame(){
		try {
			this.cadastrosApoioExamesFacade.removerAelLoteExame(loteExameId);
			this.apresentarMsgNegocio(Severity.INFO,"MENSAGEM_SUCESSO_REMOCAO_LOTE_EXAME");
			loteExameId = null;

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		} finally{
			setAelLoteExames(this.examesFacade.pesquisaLotesExamesPorLoteExameUsual(this.loteExameUsual.getSeq()));
		}
	}


	// Metódo para Suggestion Box de Grupos
	public List<AelGrupoExameUsual> pesquisarGrupos(String objPesquisa){
		return this.examesFacade.obterGrupoPorCodigoDescricao(objPesquisa);
	}

	/*Metódo para Suggestion Box de Unidade Funcional*/
	public List<AghUnidadesFuncionais> pesquisarUnidadeFuncional(String param) {
		String paramString = (String) param;
		Set<AghUnidadesFuncionais> result = new HashSet<AghUnidadesFuncionais>();
		
		try {
			result = new HashSet<AghUnidadesFuncionais>(prescricaoMedicaFacade.getListaUnidadesFuncionais(paramString));
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

		List<AghUnidadesFuncionais> resultReturn = new ArrayList<AghUnidadesFuncionais>(result);
		Collections.sort(resultReturn, new Comparator<AghUnidadesFuncionais>() {
											@Override
											public int compare(AghUnidadesFuncionais u1, AghUnidadesFuncionais u2) {
												int result = u1.getLPADAndarAlaDescricao().compareToIgnoreCase(
														u2.getLPADAndarAlaDescricao());
												return result;
											}
										}  );
		return resultReturn;
		//return this.aghuFacade.pesquisarUnidadesPorCodigoDescricao(param, true);
	}
	
	// Metódo para Suggestion Box de Agrupamentos de pesquisa
	public List<AghEspecialidades> pesquisarEspecialidades(String objPesquisa){
		return this.aghuFacade.pesquisarEspecialidadePorNomeOuSigla((String)objPesquisa);
	}
	
	/*Metódo para Suggestion Box de Exames Material Analise*/
	public List<AelExamesMaterialAnalise> listarExamesMaterialAnalise(String objPesquisa){
		return this.examesFacade.listarExamesMaterialAnalise(objPesquisa);
	}

	public AelLoteExameUsual getLoteExameUsual() {
		return loteExameUsual;
	}

	public void setLoteExameUsual(AelLoteExameUsual loteExameUsual) {
		this.loteExameUsual = loteExameUsual;
	}

	public Short getLoteExameSeq() {
		return loteExameSeq;
	}

	public void setLoteExameSeq(Short loteExameSeq) {
		this.loteExameSeq = loteExameSeq;
	}

	public AelExamesMaterialAnalise getExameMaterialAnalise() {
		return exameMaterialAnalise;
	}

	public void setExameMaterialAnalise(
			AelExamesMaterialAnalise exameMaterialAnalise) {
		this.exameMaterialAnalise = exameMaterialAnalise;
	}

	public List<AelLoteExame> getAelLoteExames() {
		return aelLoteExames;
	}

	public void setAelLoteExames(List<AelLoteExame> aelLoteExames) {
		this.aelLoteExames = aelLoteExames;
	}

	public DominioOrigemAtendimento[] getOrigensPermitidas() {
		return origensPermitidas;
	}

	public void setOrigensPermitidas(
			DominioOrigemAtendimento[] origensPermitidas) {
		this.origensPermitidas = origensPermitidas;
	}

	public AelLoteExameId getLoteExameId() {
		return loteExameId;
	}

	public void setLoteExameId(AelLoteExameId loteExameId) {
		this.loteExameId = loteExameId;
	}
}