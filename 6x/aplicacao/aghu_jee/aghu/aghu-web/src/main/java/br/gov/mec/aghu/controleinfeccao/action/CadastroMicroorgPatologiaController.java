package br.gov.mec.aghu.controleinfeccao.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import org.apache.commons.lang3.StringUtils;

import br.gov.mec.aghu.controleinfeccao.business.IControleInfeccaoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.exames.cadastrosapoio.business.ICadastrosApoioExamesFacade;
import br.gov.mec.aghu.exames.vo.ResultadoCodificadoExameVO;
import br.gov.mec.aghu.model.MciMicroorganismoPatologia;
import br.gov.mec.aghu.model.MciPatologiaInfeccao;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroMicroorgPatologiaController extends ActionController {




	/**
	 * 
	 */
	private static final long serialVersionUID = -8069892916729518955L;
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	@EJB
	private ICadastrosApoioExamesFacade cadastroExamesFacade;


	private static final String PAGINA_PATOLOGIA = "cadastroPatologiasInfeccao";

	private Boolean edicaoMicroorg;
	private Boolean edicaoExame;

	private Boolean exameSituacao;
	private Boolean microorganismoSituacao;

	private MciPatologiaInfeccao mciPatologiaInfeccao;
	private MciMicroorganismoPatologia microorganismoPatologia;
	private MciMicroorganismoPatologia microorgPatologiaSelecionado;

	private ResultadoCodificadoExameVO exame;
	private ResultadoCodificadoExameVO exameSelecionado;
	private String descMicroorg;
	private Boolean modalExclusao;
	private Boolean modalExclusaoExame;
	private String descExame;


	// modo edicao atualizar de acordo com pojo
	private ResultadoCodificadoExameVO resultadoCodificado = null;

	private List<MciMicroorganismoPatologia> microorganismoLista = new ArrayList<MciMicroorganismoPatologia>();

	private List<ResultadoCodificadoExameVO> resultadoCodificadoLista = new ArrayList<ResultadoCodificadoExameVO>();
	
	private boolean permConsultaTela = false;

	public void atualizarMicroorganismo() {
		try {
			microorganismoPatologia.setIndSituacao(DominioSituacao.getInstance(microorganismoSituacao));
			controleInfeccaoFacade.atualizarMciMicroorganismoPatologia(microorganismoPatologia,	servidorLogadoFacade.obterServidorLogado());
			microorganismoLista = controleInfeccaoFacade.buscarMicroorganismoPorSeqInfeccao(mciPatologiaInfeccao .getSeq());
			apresentarMsgNegocio(Severity.INFO,	"AMP_MENSAGEM_SUCESSO_ALTERACAO", microorganismoPatologia.getDescricao());
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		cancelarEdicao();
	}

	public void atualizarMicroorganismoExame() {
		try {
			exameSelecionado.setSituacaoExame(DominioSituacao.getInstance(exameSituacao));
			controleInfeccaoFacade.atualizarMciMicroorganismoPatologiaExame(exameSelecionado, servidorLogadoFacade.obterServidorLogado());
			carregarItensPorMicroorg();
			apresentarMsgNegocio(Severity.INFO,	"AMP_MENSAGEM_SUCESSO_ALTERACAO_EXAME", exameSelecionado.getRcdDescricao());

		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		cancelarEdicaoExame();
	}

	public List<ResultadoCodificadoExameVO> buscarResultadosCodificados(
			String param) {
		return this.returnSGWithCount(cadastroExamesFacade.buscarResultadosCodificados((String) param),buscarResultadosCodificadosCount(param));
	}

	public Long buscarResultadosCodificadosCount(String param) {
		return cadastroExamesFacade
				.buscarResultadosCodificadosCount((String) param);
	}



	public void cancelarEdicao() {
		edicaoMicroorg = false;
		microorganismoPatologia = new MciMicroorganismoPatologia();
		microorganismoSituacao = true;
		cancelarEdicaoExame();
		limparItensMicroorg();
		permConsultaTela = false;
	}

	public void cancelarEdicaoExame() {
		edicaoExame = false;
		exame = new ResultadoCodificadoExameVO();
		exameSituacao = true;
		resultadoCodificado = null;
	}

	public void cancelarExclusao() {
		modalExclusao = false;
		descMicroorg = null;
	}

	public void cancelarExclusaoExame(){
		modalExclusaoExame = false;
		descExame = null;
	}

	public void carregarItensPorMicroorg() {
		resultadoCodificadoLista = cadastroExamesFacade.buscarResultadosCodificadosPorMicroorgPatologia(microorgPatologiaSelecionado);
	}



	public void deletarMicroorganismo() {
		try {
			descMicroorg = microorgPatologiaSelecionado.getDescricao();
			
			controleInfeccaoFacade.deletarMciMicroorganismoPatologia( microorgPatologiaSelecionado, servidorLogadoFacade.obterServidorLogado());
			apresentarMsgNegocio(Severity.INFO,
					"AMP_MENSAGEM_SUCESSO_EXCLUSAO", descMicroorg);

			microorganismoLista = controleInfeccaoFacade.buscarMicroorganismoPorSeqInfeccao(mciPatologiaInfeccao.getSeq());
			limparItensMicroorg();
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		microorgPatologiaSelecionado = null;
		resultadoCodificado = null;
		exameSelecionado = null;
		// limpa os parametros depois da exclusao
		cancelarExclusao();
	}

	public void deletarMicroorganismoExame() {
		try {
			descExame = exameSelecionado.getRcdDescricao();
			
			controleInfeccaoFacade.deletarMciMicroorganismoPatologiaExame(
					exameSelecionado,
					servidorLogadoFacade.obterServidorLogado());
			apresentarMsgNegocio(Severity.INFO,
					"AMP_MENSAGEM_SUCESSO_EXCLUSAO_EXAME", descExame);
			carregarItensPorMicroorg();
			exameSelecionado = null;
			resultadoCodificado = null;
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}
		cancelarExclusaoExame();
	}

	public void editar() {
		edicaoMicroorg = true;
		microorganismoSituacao = microorgPatologiaSelecionado.getIndSituacao().isAtivo();
		microorganismoPatologia = microorgPatologiaSelecionado;
		carregarItensPorMicroorg();
	}

	public void editarExame() {
		edicaoExame = true;
		resultadoCodificado = exameSelecionado;
		exameSituacao = exameSelecionado.getSituacaoExame().isAtivo();
	}


	public String getDescExame() {
		return descExame;
	}


	public String getDescMicroorg() {
		return descMicroorg;
	}

	public Boolean getEdicaoExame() {
		return edicaoExame;
	}

	public Boolean getEdicaoMicroorg() {
		return edicaoMicroorg;
	}

	public ResultadoCodificadoExameVO getExame() {
		return exame;
	}

	public ResultadoCodificadoExameVO getExameSelecionado() {
		return exameSelecionado;
	}



	public Boolean getExameSituacao() {
		return exameSituacao;
	}

	public MciPatologiaInfeccao getMciPatologiaInfeccao() {
		return mciPatologiaInfeccao;
	}

	public List<MciMicroorganismoPatologia> getMicroorganismoLista() {
		return microorganismoLista;
	}

	public MciMicroorganismoPatologia getMicroorganismoPatologia() {
		return microorganismoPatologia;
	}

	public Boolean getMicroorganismoSituacao() {
		return microorganismoSituacao;
	}

	public MciMicroorganismoPatologia getMicroorgPatologiaSelecionado() {
		return microorgPatologiaSelecionado;
	}

	public Boolean getModalExclusao() {
		return modalExclusao;
	}


	public Boolean getModalExclusaoExame() {
		return modalExclusaoExame;
	}


	public ResultadoCodificadoExameVO getResultadoCodificado() {
		return resultadoCodificado;
	}

	public List<ResultadoCodificadoExameVO> getResultadoCodificadoLista() {
		return resultadoCodificadoLista;
	}

	public void gravarMicroorganismo() {
		if (StringUtils.isBlank(getMicroorganismoPatologia().getDescricao())) {
			apresentarMsgNegocio(Severity.ERROR,
					"AMP_MENSAGEM_MICROORG_DESC_NULO");
		} else {

			getMicroorganismoPatologia().setIndSituacao(
					DominioSituacao.getInstance(microorganismoSituacao));
			controleInfeccaoFacade.inserirMciMicroorganismoPatologia(
					mciPatologiaInfeccao, getMicroorganismoPatologia(),
					servidorLogadoFacade.obterServidorLogado());

			microorganismoLista = controleInfeccaoFacade
					.buscarMicroorganismoPorSeqInfeccao(mciPatologiaInfeccao
							.getSeq());
			apresentarMsgNegocio(Severity.INFO,
					"AMP_MENSAGEM_SUCESSO_CADASTRO",
					getMicroorganismoPatologia().getDescricao());
			microorganismoPatologia = new MciMicroorganismoPatologia();
		}
	}

	public void gravarMicroorganismoExame() {
		try {
			if (getMicroorgPatologiaSelecionado() == null) {
				apresentarMsgNegocio(Severity.ERROR,
						"AMP_MENSAGEM_MICROORG_NULO");
			} else if (resultadoCodificado == null) {
				apresentarMsgNegocio(Severity.ERROR,
						"AMP_MENSAGEM_DESC_EXAME_VAZIA");
			} else {
				exame = resultadoCodificado;
				exame.setSituacaoExame(DominioSituacao.getInstance(exameSituacao));
				controleInfeccaoFacade.inserirMciMicroorganismoPatologiaExame(getMicroorgPatologiaSelecionado(), exame, servidorLogadoFacade.obterServidorLogado());
				carregarItensPorMicroorg();
				apresentarMsgNegocio(Severity.INFO,	"AMP_MENSAGEM_SUCESSO_CADASTRO_EXAME", exame.getRcdDescricao());
				resultadoCodificado = null;
				exame = new ResultadoCodificadoExameVO();

			}
		} catch (ApplicationBusinessException e) {
			apresentarExcecaoNegocio(e);
		}

	}

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void iniciar() {
	 

	 

		if ((mciPatologiaInfeccao == null || mciPatologiaInfeccao.getSeq() == null) && !permConsultaTela) {
			mciPatologiaInfeccao = new MciPatologiaInfeccao();
		} else {
			microorganismoLista = controleInfeccaoFacade.buscarMicroorganismoPorSeqInfeccao(mciPatologiaInfeccao.getSeq());
		}
		edicaoMicroorg = false;
		edicaoExame = false;
		exameSituacao = true;
		microorganismoSituacao = true;
		microorganismoPatologia = new MciMicroorganismoPatologia();
	
	}
	

	private void limparItensMicroorg() {
		resultadoCodificadoLista = new ArrayList<ResultadoCodificadoExameVO>();
	}

	private void limparParametros() {
		edicaoMicroorg = false;
		edicaoExame = false;
		exameSituacao = true;
		microorganismoSituacao = true;
		mciPatologiaInfeccao = null;
		microorganismoPatologia = null;
		microorgPatologiaSelecionado = null;
		exame = null;
		exameSelecionado = null;
		descMicroorg = null;
		modalExclusao = null;
		modalExclusaoExame = null;
		descExame = null;
		resultadoCodificado = null;
		microorganismoLista = new ArrayList<MciMicroorganismoPatologia>();
		resultadoCodificadoLista = new ArrayList<ResultadoCodificadoExameVO>();
	}

	public void setDescExame(String descExame) {
		this.descExame = descExame;
	}

	public void setDescMicroorg(String descMicroorg) {
		this.descMicroorg = descMicroorg;
	}

	public void setEdicaoExame(Boolean edicaoExame) {
		this.edicaoExame = edicaoExame;
	}

	public void setEdicaoMicroorg(Boolean edicaoMicroorg) {
		this.edicaoMicroorg = edicaoMicroorg;
	}

	public void setExame(ResultadoCodificadoExameVO exame) {
		this.exame = exame;
	}

	public void setExameSelecionado(
			ResultadoCodificadoExameVO exameSelecionado) {
		this.exameSelecionado = exameSelecionado;
	}

	public void setExameSituacao(Boolean exameSituacao) {
		this.exameSituacao = exameSituacao;
	}

	public void setMciPatologiaInfeccao(MciPatologiaInfeccao mciPatologiaInfeccao) {
		this.mciPatologiaInfeccao = mciPatologiaInfeccao;
	}

	public void setMicroorganismoPatologia(MciMicroorganismoPatologia microorganismoPatologia) {
		this.microorganismoPatologia = microorganismoPatologia;
	}

	public void setMicroorganismoSituacao(Boolean microorganismoSituacao) {
		this.microorganismoSituacao = microorganismoSituacao;
	}



	public void setMicroorgPatologiaSelecionado(
			MciMicroorganismoPatologia microorgPatologiaSelecionado) {
		this.microorgPatologiaSelecionado = microorgPatologiaSelecionado;
	}

	public void setModalExclusao(Boolean modalExclusao) {
		this.modalExclusao = modalExclusao;
	}

	public void setModalExclusaoExame(Boolean modalExclusaoExame) {
		this.modalExclusaoExame = modalExclusaoExame;
	}


	public void setResultadoCodificado(
			ResultadoCodificadoExameVO resultadoCodificado) {
		this.resultadoCodificado = resultadoCodificado;
	}

	public void setResultadoCodificadoLista(
			List<ResultadoCodificadoExameVO> resultadoCodificadoLista) {
		this.resultadoCodificadoLista = resultadoCodificadoLista;
	}

	public String voltar() {
		limparParametros();
		return PAGINA_PATOLOGIA;
	}

	public boolean isPermConsultaTela() {
		return permConsultaTela;
	}

	public void setPermConsultaTela(boolean permConsultaTela) {
		this.permConsultaTela = permConsultaTela;
	}
}
