package br.gov.mec.aghu.faturamento.cadastrosapoio.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.FatCnesVO;
import br.gov.mec.aghu.faturamento.vo.FatUnidadeFuncionalCnesVO;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.FatCnesUf;
import br.gov.mec.aghu.model.FatServClassificacoes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class CadastroFatUnfCneController extends ActionController {


	private static final long serialVersionUID = 1471414702721922113L;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	@EJB
	private IAghuFacade aghuFacade;
	
	// Item SB1
	private AghUnidadesFuncionais unidadeFuncional;
	// Item SB2
	private FatCnesVO fatCnesVO;
	// Item selecinado na grid
	private FatUnidadeFuncionalCnesVO unfCne;
	// Lista grid
	private List<FatUnidadeFuncionalCnesVO> lista = new ArrayList<FatUnidadeFuncionalCnesVO>();
	private Boolean mostraGrid = Boolean.FALSE;
	private Boolean mostraModal = Boolean.FALSE;



	@PostConstruct
	public void init() {
		begin(conversation);
	}	
	
	public void pesquisar() {
		if(getUnidadeFuncional() != null ) {
			setLista(faturamentoFacade.pesquisarFatUnidadeFuncionalCnes(getUnidadeFuncional().getSeq()));
			mostraGrid = true;
		}
	}
	
	public void adicionar() {
		if(isFatCnesVONull()) {
			apresentarMsgNegocio(Severity.ERROR, "MENSAGEM_SG_CNES_OBRIGATORIO");
			return;
		}
		faturamentoFacade.inserirFatCnesUf(criarFatCnesUf());
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_CADASTRO_CNES", getFatCnesVO().getDescricao());		
		pesquisar();
		fatCnesVO = null;
	}
	
	private boolean isFatCnesVONull() {
		return fatCnesVO == null;
	}
	
	private FatCnesUf criarFatCnesUf() {
		FatCnesUf entity = new FatCnesUf();
		entity.setFatServClassificacoes(obterClassificacao());
		entity.setUnidadeFuncional(getUnidadeFuncional());
		return entity;
	}
	
	private FatServClassificacoes obterClassificacao() {
		return faturamentoFacade.obterServClassificacoesPorChavePrimaria(getFatCnesVO().getCodigoClassificacao());
	}	
	
	public void cancelarExclusao(){
		mostraModal = false;
	}
	public void mostrarModalExclusao() {
		mostraModal = true;
	}
	
	public void excluir() {
		AghUnidadesFuncionais unf = getUnidadeFuncional();
		Boolean delecaoPosInsercao = false;
		if(unf == null) {
			unf = aghuFacade.obterUnidadeFuncional(unfCne.getCnesUnfSeq());
			setUnidadeFuncional(unf);
			delecaoPosInsercao = true;
		}
		
		faturamentoFacade.deletarFatCnesUf(unf.getSeq(), unfCne.getClaSeq(), unfCne.getCnesSeq());
		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_EXCLUSAO_CNES", unfCne.getClaDescricao());
		pesquisar();
		if(delecaoPosInsercao) {
			setUnidadeFuncional(null);
		}
		mostraModal = false;
	}
	
	public void limparLista() {
		setLista(new ArrayList<FatUnidadeFuncionalCnesVO>());
		fatCnesVO = null;
		mostraGrid = false;
	}
	
	public void limparParametros() {
		setUnidadeFuncional(null);
		setFatCnesVO(null);
		unfCne = null;
		mostraGrid = false;
	}
	
	/**
	 * SB campo 1. 
	 * @param param
	 * @return
	 */
	public List<AghUnidadesFuncionais> pesquisarUnidadesFuncionais(String param) {
		return aghuFacade.obterUnidadesFuncionaisSB(param);
	}
	
	public Long pesquisarUnidadesFuncionaisCount(String param) {
		return aghuFacade.obterUnidadesFuncionaisSBCount(param);
	}
	
	/**
	 * SB campo 2. 
	 * @param param
	 * @return
	 */
	public List<FatCnesVO> pesquisarCnes(String param) {
		return faturamentoFacade.pesquisarFatCnesPorSeqDescricao(param);
	}
	
	public Long pesquisarCnesCount(String param) {
		return faturamentoFacade.pesquisarFatCnesPorSeqDescricaoCount(param);
	}

	public FatUnidadeFuncionalCnesVO getUnfCne() {
		return unfCne;
	}

	public void setUnfCne(FatUnidadeFuncionalCnesVO unfCne) {
		this.unfCne = unfCne;
	}

	public List<FatUnidadeFuncionalCnesVO> getLista() {
		return lista;
	}

	public void setLista(List<FatUnidadeFuncionalCnesVO> lista) {
		this.lista = lista;
	}

	public AghUnidadesFuncionais getUnidadeFuncional() {
		return unidadeFuncional;
	}

	public void setUnidadeFuncional(AghUnidadesFuncionais unidadeFuncional) {
		this.unidadeFuncional = unidadeFuncional;
	}

	public FatCnesVO getFatCnesVO() {
		return fatCnesVO;
	}

	public void setFatCnesVO(FatCnesVO fatCnesVO) {
		this.fatCnesVO = fatCnesVO;
	}

	public Boolean getMostraGrid() {
		return mostraGrid;
	}

	public void setMostraGrid(Boolean mostraGrid) {
		this.mostraGrid = mostraGrid;
	}

	public Boolean getMostraModal() {
		return mostraModal;
	}

	public void setMostraModal(Boolean mostraModal) {
		this.mostraModal = mostraModal;
	}

}
