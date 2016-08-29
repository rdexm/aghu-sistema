package br.gov.mec.aghu.sicon.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacaoEnvioContrato;
import br.gov.mec.aghu.dominio.DominioSortableSitEnvioContrato;
import br.gov.mec.aghu.model.ScoAditContrato;
import br.gov.mec.aghu.sicon.business.ISiconFacade;
import br.gov.mec.aghu.sicon.vo.ContratoFiltroVO;
import br.gov.mec.aghu.sicon.vo.ContratoGridVO;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.BaseException;

public class AditivoIntegracaoSiconController extends ActionController {

	private static final long serialVersionUID = -945457096213119006L;

	@EJB
	private ISiconFacade siconFacade;

	private List<ContratoGridVO> aditivos; // Grid da aba de Aditivos

	private ContratoGridVO aditSelecionado;

	// Radio Button - Parâmetros de seleção de aditivo
	private Integer seqAditSelecionado;
	private Integer contSeqAditSelecionado;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	/**
	 * Método que executa a segunda pesquisa
	 * 
	 * Se na pesquisa inicial de contratos não retornaram registros, os aditivos
	 * serão pesquisados através do filtro (objeto-VO) geral de pesquisa.
	 * 
	 * @param ContratoFiltroVO
	 *            - Filtro populado na tela principal
	 */
	public void pesquisarAditivos(ContratoFiltroVO filtroContratoIntegracao) throws BaseException {

		limpar();

		// Consulta Aditivos através do filtro geral
		List<ScoAditContrato> aditivosContratos = siconFacade.pesquisarAditivos(filtroContratoIntegracao);

		if (aditivosContratos != null && aditivosContratos.size() > 0) {

			aditivos = setDadosVO(aditivosContratos);
		}
	}

	public void limpar() {

		setAditSelecionado(null);

		if (aditivos != null) {
			aditivos.clear();
		} else {
			aditivos = new ArrayList<ContratoGridVO>();
		}

	}

	private List<ContratoGridVO> setDadosVO(List<ScoAditContrato> aditivosContratos) {

		List<ContratoGridVO> grid = new ArrayList<ContratoGridVO>();

		// Popula VO utilizado na Grid
		for (ScoAditContrato aditContrato : aditivosContratos) {

			ContratoGridVO vo = new ContratoGridVO(aditContrato);

			if (aditContrato.getSituacao() == DominioSituacaoEnvioContrato.A
					|| aditContrato.getSituacao() == DominioSituacaoEnvioContrato.AR) {
				vo.setSitenvio(DominioSortableSitEnvioContrato.YELLOW);
				vo.setPendenciasTooltip("Aditivo aguardando envio para o SICON.");

			} else if (aditContrato.getSituacao() == DominioSituacaoEnvioContrato.E) {
				vo.setSitenvio(DominioSortableSitEnvioContrato.GREEN);
				vo.setPendenciasTooltip("Aditivo enviado com sucesso para o SICON.");

			} else if (aditContrato.getSituacao() == DominioSituacaoEnvioContrato.EE) {
				vo.setSitenvio(DominioSortableSitEnvioContrato.RED);
				vo.setPendenciasTooltip("Erro(s) no envio do contrato para o SICON.");
			}

			grid.add(vo);
		}

		return grid;
	}

	// Getters and Setters
	public List<ContratoGridVO> getAditivos() {
		return aditivos;
	}

	public void setAditivos(List<ContratoGridVO> aditivos) {
		this.aditivos = aditivos;
	}

	// Getters e Setters para aditivo Selecionado por Radio Button

	public ContratoGridVO getAditSelecionado() {
		return aditSelecionado;
	}

	public void setAditSelecionado(ContratoGridVO aditSelecionado) {
		this.aditSelecionado = aditSelecionado;
	}

	public Integer getSeqAditSelecionado() {
		return seqAditSelecionado;
	}

	public void setSeqAditSelecionado(Integer seqAditSelecionado) {
		this.seqAditSelecionado = seqAditSelecionado;
	}

	public Integer getContSeqAditSelecionado() {
		return contSeqAditSelecionado;
	}

	public void setContSeqAditSelecionado(Integer contSeqAditSelecionado) {
		this.contSeqAditSelecionado = contSeqAditSelecionado;
		setAditSelecionado();
	}

	private void setAditSelecionado() {

		if (getSeqAditSelecionado() != null && getContSeqAditSelecionado() != null) {

			for (ContratoGridVO aditVO : aditivos) {

				if (aditVO.getAditContrato() != null && aditVO.getAditContrato().getId() != null
						&& aditVO.getAditContrato().getId().getSeq().equals(this.getSeqAditSelecionado())
						&& aditVO.getAditContrato().getId().getContSeq().equals(this.getContSeqAditSelecionado())) {

					setAditSelecionado(aditVO);

					break;
				}
			}

		} else {
			setAditSelecionado(null);
		}
	}

}
