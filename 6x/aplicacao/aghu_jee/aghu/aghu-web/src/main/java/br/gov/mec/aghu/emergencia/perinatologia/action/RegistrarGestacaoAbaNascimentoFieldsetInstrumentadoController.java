package br.gov.mec.aghu.emergencia.perinatologia.action;

import java.util.ArrayList;
import java.util.List;

import javax.inject.Inject;

import br.gov.mec.aghu.emergencia.business.IEmergenciaFacade;
import br.gov.mec.aghu.model.McoForcipes;
import br.gov.mec.aghu.model.McoIndicacaoNascimento;
import br.gov.mec.aghu.model.McoNascIndicacoes;
import br.gov.mec.aghu.perinatologia.vo.DadosNascimentoSelecionadoVO;
import br.gov.mec.aghu.perinatologia.vo.IndicacaoPartoVO;
import br.gov.mec.aghu.core.action.ActionController;

public class RegistrarGestacaoAbaNascimentoFieldsetInstrumentadoController extends ActionController {
	private static final long serialVersionUID = -4858884771232547892L;

	@Inject
	private IEmergenciaFacade emergenciaFacade;

	@Inject
	private RegistrarGestacaoAbaNascimentoController registrarGestacaoAbaNascimentoController;
	
	private Integer gsoPacCodigo;
	private Short gsoSeqp;
	private Integer seqp;

	// McoIndicacaoNascimento selecionada na suggestionbox
	private McoIndicacaoNascimento indicacaoNascimento;

	// Lista de McoIndicacaoNascimento
	private List<IndicacaoPartoVO> listaNascIndicacoes = new ArrayList<IndicacaoPartoVO>();
	
	private McoForcipes mcoForcipesExcluir;
	private List<McoNascIndicacoes> listaNascIndicacoesRemover = new ArrayList<McoNascIndicacoes>();

	// Utilizado para remover item da lista
	private IndicacaoPartoVO indicacaoPartoVORemover;
	
	private Integer idProximoElemento = 1;
	
	private DadosNascimentoSelecionadoVO dadosNascimentoSelecionado;
	private McoForcipes mcoForcipesOriginal;

	/* -------------------------------------------------------------------------------------- */

	/**
	 * Suggestion Indicação - PESQUISA
	 * 
	 * @param param
	 * @return
	 */
	public List<McoIndicacaoNascimento> pesquisarIndicacoes(String param) {
		return  this.returnSGWithCount(emergenciaFacade.pesquisarMcoIndicacaoNascimentoAtivosPorDescricaoOuSeq((String) param, 100),pesquisarIndicacoesCount(param));
	}

	/**
	 * Suggestion Indicação - COUNT
	 * 
	 * @param param
	 * @return
	 */
	public Long pesquisarIndicacoesCount(String param) {
		return emergenciaFacade.pesquisarMcoIndicacaoNascimentoAtivosPorDescricaoOuSeqCount((String) param);
	}

	/**
	 * Botão ADICIONAR
	 */
	public void adicionarIndicacao() {
		this.houveAlteracao();
		if (indicacaoNascimento != null) {
			McoNascIndicacoes mcoNascIndicacoes = new McoNascIndicacoes();
			mcoNascIndicacoes.setIndicacaoNascimento(indicacaoNascimento);
			listaNascIndicacoes.add(new IndicacaoPartoVO(this.idProximoElemento++, mcoNascIndicacoes));
			indicacaoNascimento = null;
		}
	}

	/**
	 * Botão REMOVER
	 */
	public void removerIndicacao() {
		this.houveAlteracao();
		if (indicacaoPartoVORemover != null) {
			listaNascIndicacoes.remove(indicacaoPartoVORemover);
			if (indicacaoPartoVORemover.getMcoNascIndicacoes().getSeq() != null) {
				this.listaNascIndicacoesRemover.add(indicacaoPartoVORemover.getMcoNascIndicacoes());
			}
			indicacaoPartoVORemover = null;
		}
	}

	/* -------------------------------------------------------------------------------------- */

	/**
	 * Utilizado para inicializar o fieldset
	 */
	public void prepararFieldset(final Integer gsoPacCodigo, final Short gsoSeqp, final Integer seqp,
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado, McoForcipes mcoForcipesOriginal) {
		this.limparFieldset();
		this.gsoPacCodigo = gsoPacCodigo;
		this.gsoSeqp = gsoSeqp;
		this.seqp = seqp;
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
		this.mcoForcipesOriginal = mcoForcipesOriginal;
		
		List<McoNascIndicacoes> original = emergenciaFacade.pesquisarIndicacoesPartoInstrumentado(gsoPacCodigo, gsoSeqp, seqp);
		for (McoNascIndicacoes mcoNascIndicacoes : original) {
			listaNascIndicacoes.add(new IndicacaoPartoVO(this.idProximoElemento++, mcoNascIndicacoes));
		}
	}
	
	/**
	 * Utilizado para inicializar o fieldset
	 */
	public void limparFieldset() {
		listaNascIndicacoes.clear();
		this.idProximoElemento = 1;
		listaNascIndicacoesRemover.clear();
	}

	/**
	 * Recupera a lista de McoNascIndicacoes, pois o fieldset utiliza VO.
	 * 
	 * @return
	 */
	public List<McoNascIndicacoes> obterListaMcoNascIndicacoes() {
		List<McoNascIndicacoes> result = new ArrayList<McoNascIndicacoes>();
		for (IndicacaoPartoVO indicacaoPartoVO : listaNascIndicacoes) {
			result.add(indicacaoPartoVO.getMcoNascIndicacoes());
		}
		return result;
	}
	
	public void limparCampos() {
		this.houveAlteracao();
		this.limparFieldset();
		List<McoNascIndicacoes> original = emergenciaFacade.pesquisarIndicacoesPartoInstrumentado(this.gsoPacCodigo, this.gsoSeqp, this.seqp);
		// Deve remover todos os registros que estão no banco.
		listaNascIndicacoesRemover.addAll(original);
		// Limpa os campos da Cesariana.
		this.dadosNascimentoSelecionado.setMcoForcipe(new McoForcipes());
		// Mantém o McoCesarianas na memória para excluir depois.
		if (this.mcoForcipesOriginal.getId() != null) {
			this.mcoForcipesExcluir = this.mcoForcipesOriginal;
		}
	}
	
	public void houveAlteracao() {
		this.registrarGestacaoAbaNascimentoController.setHouveAlteracao(Boolean.TRUE);
	}

	/* -------------------------------------------------------------------------------------- */

	public boolean isPermExecutarIndicacoesAbaNascimento() {
		return registrarGestacaoAbaNascimentoController.isPermExecutarIndicacoesAbaNascimento();
	}

	/* -------------------------------------------------------------------------------------- */

	public Integer getGsoPacCodigo() {
		return gsoPacCodigo;
	}

	public void setGsoPacCodigo(Integer gsoPacCodigo) {
		this.gsoPacCodigo = gsoPacCodigo;
	}

	public Short getGsoSeqp() {
		return gsoSeqp;
	}

	public void setGsoSeqp(Short gsoSeqp) {
		this.gsoSeqp = gsoSeqp;
	}

	public Integer getSeqp() {
		return seqp;
	}

	public void setSeqp(Integer seqp) {
		this.seqp = seqp;
	}
	
	public McoIndicacaoNascimento getIndicacaoNascimento() {
		return indicacaoNascimento;
	}

	public void setIndicacaoNascimento(McoIndicacaoNascimento indicacaoNascimento) {
		this.indicacaoNascimento = indicacaoNascimento;
	}

	public List<IndicacaoPartoVO> getListaNascIndicacoes() {
		return listaNascIndicacoes;
	}

	public void setListaNascIndicacoes(List<IndicacaoPartoVO> listaNascIndicacoes) {
		this.listaNascIndicacoes = listaNascIndicacoes;
	}

	public McoForcipes getMcoForcipesExcluir() {
		return mcoForcipesExcluir;
	}

	public void setMcoForcipesExcluir(McoForcipes mcoForcipesExcluir) {
		this.mcoForcipesExcluir = mcoForcipesExcluir;
	}

	public List<McoNascIndicacoes> getListaNascIndicacoesRemover() {
		return listaNascIndicacoesRemover;
	}

	public void setListaNascIndicacoesRemover(
			List<McoNascIndicacoes> listaNascIndicacoesRemover) {
		this.listaNascIndicacoesRemover = listaNascIndicacoesRemover;
	}

	public IndicacaoPartoVO getIndicacaoPartoVORemover() {
		return indicacaoPartoVORemover;
	}

	public void setIndicacaoPartoVORemover(IndicacaoPartoVO indicacaoPartoVORemover) {
		this.indicacaoPartoVORemover = indicacaoPartoVORemover;
	}
	
	public DadosNascimentoSelecionadoVO getDadosNascimentoSelecionado() {
		return dadosNascimentoSelecionado;
	}

	public void setDadosNascimentoSelecionado(
			DadosNascimentoSelecionadoVO dadosNascimentoSelecionado) {
		this.dadosNascimentoSelecionado = dadosNascimentoSelecionado;
	}

	public McoForcipes getMcoForcipesOriginal() {
		return mcoForcipesOriginal;
	}

	public void setMcoForcipesOriginal(McoForcipes mcoForcipesOriginal) {
		this.mcoForcipesOriginal = mcoForcipesOriginal;
	}
}