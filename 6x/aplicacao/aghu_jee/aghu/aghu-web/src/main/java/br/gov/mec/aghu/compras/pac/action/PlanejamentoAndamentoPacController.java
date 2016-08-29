package br.gov.mec.aghu.compras.pac.action;

import java.util.Date;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;

import br.gov.mec.aghu.compras.business.IComprasFacade;
import br.gov.mec.aghu.compras.cadastrosbasicos.business.IComprasCadastrosBasicosFacade;
import br.gov.mec.aghu.compras.pac.business.IPacFacade;
import br.gov.mec.aghu.compras.vo.EtapaPACVO;
import br.gov.mec.aghu.compras.vo.EtapasRelacionadasPacVO;
import br.gov.mec.aghu.compras.vo.HistoricoLogEtapaPacVO;
import br.gov.mec.aghu.compras.vo.LocalPACVO;
import br.gov.mec.aghu.compras.vo.ModPacSolicCompraServicoVO;
import br.gov.mec.aghu.dominio.DominioSituacaoEtapaPac;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.model.ScoEtapaPac;
import br.gov.mec.aghu.model.ScoLicitacao;
import br.gov.mec.aghu.model.ScoLocalizacaoProcesso;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.exception.Severity;

public class PlanejamentoAndamentoPacController extends ActionController {

	private static final String LOCAL = "Local: ";

	private static final String ETAPA = "Etapa: ";

	private static final long serialVersionUID = -7327874025097037867L;

	@EJB
	private IPacFacade pacFacade;

	@EJB
	private IComprasFacade comprasFacade;

	@EJB
	private IRegistroColaboradorFacade registroColaboradorFacade;

	@EJB
	private IComprasCadastrosBasicosFacade comprasCadastrosBasicosFacade;

	private static final String REDIRECT_PAGE_ANDAMENTO_PROCESSO_COMPRA = "andamentoPacList";

	private ScoLicitacao licitacao;
	private Integer licitacaoId;
	private ModPacSolicCompraServicoVO modPacSolicCompraServicoVO;
	private DominioSituacaoEtapaPac situacaoEtapa;
	private List<EtapasRelacionadasPacVO> etapasRelacionadasPacVO;

	private Boolean acompanharHistorico;

	private LocalPACVO localPACVO;
	private RapServidores servidor;
	private EtapaPACVO etapaVO;
	private Date dataPac;

	// Modal Nova Etapa
	private ScoLocalizacaoProcesso localNovaEtapa;
	private String numeroDescricaoPac;
	private String descricaoNovaEtapa;
	private Short novaEtapaTempoPrevisto;

	// Modal Historico Etapa
	private List<HistoricoLogEtapaPacVO> listaHistoricoEtapa;
	private String localHistorico;
	private String etapaHistorico;

	// Modal Visualizar Etapa
	private DominioSituacaoEtapaPac situacaoEtapaVisualizar;
	private String descricaoUsuarioVisualizar;
	private Date dataPacVisualizar;
	private String descricaoObsVisualizar;

	// Modal Atualizar Etapa
	private DominioSituacaoEtapaPac situacaoEtapaAtualizar;
	private Short tempoPrevistoAtualizar;
	private String descricaoObsAtualizar;
	private Integer codigoEtapaAtlz;

	private Boolean houveAlteracaoNovaEtapa = Boolean.FALSE;
	private Boolean houveAlteracaoAtlzEtapa = Boolean.FALSE;
	private EtapasRelacionadasPacVO itemAtlz;

	@PostConstruct
	protected void inicializar() {
		this.begin(conversation);
	}

	public void inicio() {
	 

	 


		if (this.licitacaoId != null) {
			this.licitacao = this.comprasFacade
					.buscarLicitacaoPorNumero(this.licitacaoId);
			this.obterDadosNumeroPac();
		}

		this.setAcompanharHistorico(Boolean.FALSE);
	
	}
	

	public List<ScoLicitacao> pesquisarLicitacoesPorNumeroDescricao(
			String licitacao) {
		return this.pacFacade.pesquisarLicitacoesPorNumeroDescricao(licitacao);
	}

	public void obterDadosNumeroPac() {

		if (this.licitacao != null) {
			List<ScoEtapaPac> etapaPac = this.comprasFacade
					.obterEtapaPacPorLicitacao(this.licitacao.getNumero());

			if (etapaPac == null || etapaPac.isEmpty()) {
				this.setAcompanharHistorico(Boolean.TRUE);
			}
		}

		this.modPacSolicCompraServicoVO = this.comprasFacade
				.obterModaldiadePacSocilitacao(this.licitacao.getNumero(),
						this.licitacao.getModalidadeLicitacao().getCodigo());
	}

	public void limparDadosNumeroPac() {
		this.setAcompanharHistorico(Boolean.FALSE);
		this.setModPacSolicCompraServicoVO(null);
		this.setEtapasRelacionadasPacVO(null);
		this.setLicitacao(null);
		this.setDataPac(null);
		this.setLocalPACVO(null);
		this.setServidor(null);
		this.setSituacaoEtapa(null);
		this.setEtapaVO(null);
	}

	public void acompanharHistorico() {
		this.setAcompanharHistorico(Boolean.FALSE);
		this.etapasRelacionadasPacVO = this.comprasFacade
				.acompanharHistorico(this.licitacao.getNumero(),
						this.modPacSolicCompraServicoVO.getDescricaoObjeto(),
						this.modPacSolicCompraServicoVO.getCodigoModalidade());
	}

	public List<LocalPACVO> pesquisarLocaisPac(String local) {
		return this.comprasFacade.pesquisarLocaisPac(
				this.licitacao.getNumero(),
				this.modPacSolicCompraServicoVO.getCodigoModalidade(),
				this.modPacSolicCompraServicoVO.getDescricaoObjeto());
	}

	public List<RapServidores> pesquisarServidores(String objServidor) {
		try {
			return this.registroColaboradorFacade
					.pesquisarServidoresVinculados(objServidor);
		} catch (BaseException e) {
			this.apresentarExcecaoNegocio(e);
		}
		return null;
	}

	public List<EtapaPACVO> pesquisarEtapasPac(String etapa) {
		return this.comprasFacade.pesquisaEtapasPac(etapa,
				this.licitacao.getNumero(), this.localPACVO,
				this.modPacSolicCompraServicoVO.getDescricaoObjeto(),
				this.modPacSolicCompraServicoVO.getCodigoModalidade());
	}

	public void pesquisar() {

		RapServidoresId idServidor = null;

		if (this.servidor != null) {
			idServidor = this.servidor.getId();
		}

		this.etapasRelacionadasPacVO = this.comprasFacade.pesquisarEtapas(
				this.licitacao.getNumero(), this.modPacSolicCompraServicoVO,
				this.situacaoEtapa, this.localPACVO, idServidor, this.etapaVO,
				this.dataPac);
	}

	public DominioSituacaoEtapaPac[] listarSituacoesEtapa() {
		return DominioSituacaoEtapaPac.values();
	}

	public String encaminhar() {

		this.setLicitacaoId(this.licitacao.getNumero());
		return REDIRECT_PAGE_ANDAMENTO_PROCESSO_COMPRA;
	}

	public void novaEtapa() {
		String descricaoPac = this.getLicitacao().getNumero().toString()
				.concat(" - ").concat(this.getLicitacao().getDescricao());
		this.setNumeroDescricaoPac(descricaoPac);
	}

	public void gravarNovaEtapa() {
		try {

			this.comprasFacade.gravarNovaEtapa(this.localNovaEtapa, this
					.getLicitacao().getNumero(), this.descricaoNovaEtapa,
					this.novaEtapaTempoPrevisto);
			
			this.apresentarMsgNegocio(Severity.INFO, "ETAPA_CRIADA_SUCESSO",
					this.localNovaEtapa.getDescricao(), this.descricaoNovaEtapa);
			
			this.restaurarNovaEtapa();
			this.pesquisar();
			

		} catch (ApplicationBusinessException e) {
			this.apresentarExcecaoNegocio(e);
		}
	}

	private void restaurarNovaEtapa() {
		this.setLocalNovaEtapa(null);
		this.setNumeroDescricaoPac(null);
		this.setDescricaoNovaEtapa(null);
		this.setNovaEtapaTempoPrevisto(null);
	}

	public void atualizar(EtapasRelacionadasPacVO item) {
		this.setItemAtlz(item);
		this.setLocalHistorico(LOCAL.concat(item.getDescricaoLocProcesso()));
		this.setEtapaHistorico(ETAPA.concat(item.getDescricaoEtapa()));
		this.setCodigoEtapaAtlz(item.getCodigoEtapa());
		this.setSituacaoEtapaAtualizar(item.getSituacao());
		this.setTempoPrevistoAtualizar(item.getTempoPrevisto());
		this.setDescricaoObsAtualizar(item.getApontamentoUsuario());
	}

	public void atualizarEtapa() {
		this.comprasFacade.atualizarEtapa(this.codigoEtapaAtlz,
				this.situacaoEtapaAtualizar, this.tempoPrevistoAtualizar,
				this.descricaoObsAtualizar);
		this.pesquisar();
		this.apresentarMsgNegocio(Severity.INFO, "ETAPA_ATUALIZADA_SUCESSO");
	}

	public void visualizarEtapa(EtapasRelacionadasPacVO item) {
		this.setLocalHistorico(LOCAL.concat(item.getDescricaoLocProcesso()));
		this.setEtapaHistorico(ETAPA.concat(item.getDescricaoEtapa()));
		this.setSituacaoEtapaVisualizar(item.getSituacao());
		this.setDescricaoUsuarioVisualizar(item.getNome());
		this.setDataPacVisualizar(item.getDataApontamento());
		this.setDescricaoObsVisualizar(item.getApontamentoUsuario());
	}

	public List<ScoLocalizacaoProcesso> pesquisarLocalizacaoProcessoPorCodigoOuDescricao(
			String local) {
		return this.comprasCadastrosBasicosFacade
				.pesquisarLocalizacaoProcessoPorCodigoOuDescricao(local, null);
	}

	public void pesquisarHistoricoEtapa(
			EtapasRelacionadasPacVO item) {
		this.listaHistoricoEtapa = this.comprasFacade
				.pesquisarHistoricoEtapa(item.getCodigoEtapa());

		for (HistoricoLogEtapaPacVO vo : this.listaHistoricoEtapa) {
			this.setLocalHistorico(LOCAL.concat(vo
					.getDescricaoLocProcesso()));
			this.setEtapaHistorico(ETAPA.concat(vo.getDescricaoEtapa()));
		}
		
	}

	public void verificarHouveAlteracaoNovaEtapa() {
		if (listaHistoricoEtapa != null){
			for (HistoricoLogEtapaPacVO vo : this.listaHistoricoEtapa) {
				this.setLocalHistorico(LOCAL.concat(vo
						.getDescricaoLocProcesso()));
				this.setEtapaHistorico(ETAPA.concat(vo.getDescricaoEtapa()));
				if (this.localNovaEtapa != null) {
					this.setHouveAlteracaoNovaEtapa(Boolean.TRUE);
				} else if (this.descricaoNovaEtapa != null) {
					this.setHouveAlteracaoNovaEtapa(Boolean.TRUE);
				} else if (this.novaEtapaTempoPrevisto != null) {
					this.setHouveAlteracaoNovaEtapa(Boolean.TRUE);
				} else {
					this.setHouveAlteracaoNovaEtapa(Boolean.FALSE);
				}
			}
		}
	}

	public void verificarHouveAlteracaoAtlzEtapa() {

		if (!this.situacaoEtapaAtualizar.equals(this.itemAtlz.getSituacao())) {
			this.setHouveAlteracaoAtlzEtapa(Boolean.TRUE);
		} else if (this.tempoPrevistoAtualizar != this.itemAtlz
				.getTempoPrevisto()) {
			this.setHouveAlteracaoAtlzEtapa(Boolean.TRUE);
		} else if (this.descricaoObsAtualizar != this.itemAtlz
				.getApontamentoUsuario()) {
			this.setHouveAlteracaoAtlzEtapa(Boolean.TRUE);
		} else {
			this.setHouveAlteracaoAtlzEtapa(Boolean.FALSE);
		}

	}

	public void cancelarEdicao() {
		this.setHouveAlteracaoAtlzEtapa(Boolean.FALSE);
		this.setHouveAlteracaoNovaEtapa(Boolean.FALSE);
		this.restaurarNovaEtapa();
	}

	// Getters and Setters

	public Boolean getHouveAlteracaoNovaEtapa() {
		return houveAlteracaoNovaEtapa;
	}

	public void setHouveAlteracaoNovaEtapa(Boolean houveAlteracaoNovaEtapa) {
		this.houveAlteracaoNovaEtapa = houveAlteracaoNovaEtapa;
	}

	public Boolean getHouveAlteracaoAtlzEtapa() {
		return houveAlteracaoAtlzEtapa;
	}

	public void setHouveAlteracaoAtlzEtapa(Boolean houveAlteracaoAtlzEtapa) {
		this.houveAlteracaoAtlzEtapa = houveAlteracaoAtlzEtapa;
	}

	public EtapasRelacionadasPacVO getItemAtlz() {
		return itemAtlz;
	}

	public void setItemAtlz(EtapasRelacionadasPacVO itemAtlz) {
		this.itemAtlz = itemAtlz;
	}

	public ScoLicitacao getLicitacao() {
		return licitacao;
	}

	public void setLicitacao(ScoLicitacao licitacao) {
		this.licitacao = licitacao;
	}

	public Integer getLicitacaoId() {
		return licitacaoId;
	}

	public void setLicitacaoId(Integer licitacaoId) {
		this.licitacaoId = licitacaoId;
	}

	public DominioSituacaoEtapaPac getSituacaoEtapa() {
		return situacaoEtapa;
	}

	public void setSituacaoEtapa(DominioSituacaoEtapaPac situacaoEtapa) {
		this.situacaoEtapa = situacaoEtapa;
	}

	public ModPacSolicCompraServicoVO getModPacSolicCompraServicoVO() {
		return modPacSolicCompraServicoVO;
	}

	public void setModPacSolicCompraServicoVO(
			ModPacSolicCompraServicoVO modPacSolicCompraServicoVO) {
		this.modPacSolicCompraServicoVO = modPacSolicCompraServicoVO;
	}

	public Boolean getAcompanharHistorico() {
		return acompanharHistorico;
	}

	public void setAcompanharHistorico(Boolean acompanharHistorico) {
		this.acompanharHistorico = acompanharHistorico;
	}

	public List<EtapasRelacionadasPacVO> getEtapasRelacionadasPacVO() {
		return etapasRelacionadasPacVO;
	}

	public void setEtapasRelacionadasPacVO(
			List<EtapasRelacionadasPacVO> etapasRelacionadasPacVO) {
		this.etapasRelacionadasPacVO = etapasRelacionadasPacVO;
	}

	public LocalPACVO getLocalPACVO() {
		return localPACVO;
	}

	public void setLocalPACVO(LocalPACVO localPACVO) {
		this.localPACVO = localPACVO;
	}

	public RapServidores getServidor() {
		return servidor;
	}

	public void setServidor(RapServidores servidor) {
		this.servidor = servidor;
	}

	public EtapaPACVO getEtapaVO() {
		return etapaVO;
	}

	public void setEtapaVO(EtapaPACVO etapaVO) {
		this.etapaVO = etapaVO;
	}

	public Date getDataPac() {
		return dataPac;
	}

	public void setDataPac(Date dataPac) {
		this.dataPac = dataPac;
	}

	public ScoLocalizacaoProcesso getLocalNovaEtapa() {
		return localNovaEtapa;
	}

	public void setLocalNovaEtapa(ScoLocalizacaoProcesso localNovaEtapa) {
		this.localNovaEtapa = localNovaEtapa;
	}

	public String getNumeroDescricaoPac() {
		return numeroDescricaoPac;
	}

	public void setNumeroDescricaoPac(String numeroDescricaoPac) {
		this.numeroDescricaoPac = numeroDescricaoPac;
	}

	public String getDescricaoNovaEtapa() {
		return descricaoNovaEtapa;
	}

	public void setDescricaoNovaEtapa(String descricaoNovaEtapa) {
		this.descricaoNovaEtapa = descricaoNovaEtapa;
	}

	public Short getNovaEtapaTempoPrevisto() {
		return novaEtapaTempoPrevisto;
	}

	public void setNovaEtapaTempoPrevisto(Short novaEtapaTempoPrevisto) {
		this.novaEtapaTempoPrevisto = novaEtapaTempoPrevisto;
	}

	public List<HistoricoLogEtapaPacVO> getListaHistoricoEtapa() {
		return listaHistoricoEtapa;
	}

	public void setListaHistoricoEtapa(
			List<HistoricoLogEtapaPacVO> listaHistoricoEtapa) {
		this.listaHistoricoEtapa = listaHistoricoEtapa;
	}

	public String getLocalHistorico() {
		return localHistorico;
	}

	public void setLocalHistorico(String localHistorico) {
		this.localHistorico = localHistorico;
	}

	public String getEtapaHistorico() {
		return etapaHistorico;
	}

	public void setEtapaHistorico(String etapaHistorico) {
		this.etapaHistorico = etapaHistorico;
	}

	public DominioSituacaoEtapaPac getSituacaoEtapaVisualizar() {
		return situacaoEtapaVisualizar;
	}

	public void setSituacaoEtapaVisualizar(
			DominioSituacaoEtapaPac situacaoEtapaVisualizar) {
		this.situacaoEtapaVisualizar = situacaoEtapaVisualizar;
	}

	public String getDescricaoUsuarioVisualizar() {
		return descricaoUsuarioVisualizar;
	}

	public void setDescricaoUsuarioVisualizar(String descricaoUsuarioVisualizar) {
		this.descricaoUsuarioVisualizar = descricaoUsuarioVisualizar;
	}

	public Date getDataPacVisualizar() {
		return dataPacVisualizar;
	}

	public void setDataPacVisualizar(Date dataPacVisualizar) {
		this.dataPacVisualizar = dataPacVisualizar;
	}

	public String getDescricaoObsVisualizar() {
		return descricaoObsVisualizar;
	}

	public void setDescricaoObsVisualizar(String descricaoObsVisualizar) {
		this.descricaoObsVisualizar = descricaoObsVisualizar;
	}

	public DominioSituacaoEtapaPac getSituacaoEtapaAtualizar() {
		return situacaoEtapaAtualizar;
	}

	public void setSituacaoEtapaAtualizar(
			DominioSituacaoEtapaPac situacaoEtapaAtualizar) {
		this.situacaoEtapaAtualizar = situacaoEtapaAtualizar;
	}

	public Short getTempoPrevistoAtualizar() {
		return tempoPrevistoAtualizar;
	}

	public void setTempoPrevistoAtualizar(Short tempoPrevistoAtualizar) {
		this.tempoPrevistoAtualizar = tempoPrevistoAtualizar;
	}

	public String getDescricaoObsAtualizar() {
		return descricaoObsAtualizar;
	}

	public void setDescricaoObsAtualizar(String descricaoObsAtualizar) {
		this.descricaoObsAtualizar = descricaoObsAtualizar;
	}

	public Integer getCodigoEtapaAtlz() {
		return codigoEtapaAtlz;
	}

	public void setCodigoEtapaAtlz(Integer codigoEtapaAtlz) {
		this.codigoEtapaAtlz = codigoEtapaAtlz;
	}

}
