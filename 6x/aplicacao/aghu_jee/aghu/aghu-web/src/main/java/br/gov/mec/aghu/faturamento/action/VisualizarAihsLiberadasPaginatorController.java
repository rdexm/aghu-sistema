package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioSituacaoAih;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.AihVO;
import br.gov.mec.aghu.model.FatAih;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatProcedimentosHospitalares;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BaseException;

public class VisualizarAihsLiberadasPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = -6657828349500375975L;
	
	private static final Log LOG = LogFactory.getLog(VisualizarAihsLiberadasPaginatorController.class);
	
	@Inject @Paginator
	private DynamicDataModel<AihVO> dataModel;
	
	private FatAih fatAih;
	private List<DominioSituacaoAih> situacoes;

	private Integer cthSeq;
	private Integer serMatricula;
	private Short serVinCodigo;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	@PostConstruct
	public void inicio() {
		begin(conversation, true);
		
		situacoes = new ArrayList<DominioSituacaoAih>();
		situacoes.add(DominioSituacaoAih.A);
		situacoes.add(DominioSituacaoAih.L);
		situacoes.add(DominioSituacaoAih.R);
		
		this.init();
	}

	public void init() {			
		setFatAih(new FatAih());
		setSerMatricula(null);
		setSerVinCodigo(null);
		setCthSeq(null);
		getFatAih().setContaHospitalar(new FatContasHospitalares());
		getFatAih().setIndSituacao(DominioSituacaoAih.L);	
	}

	public String iniciarInclusao() {
		return "iniciarInclusao";
	}

	@Override
	public Long recuperarCount() {
		return faturamentoFacade.pesquisarAihsLiberadasCount(getFatAih().getNroAih(), getCthSeq(), obterDominios(getFatAih().getIndSituacao()), getFatAih().getDthrEmissao(),
				getSerVinCodigo(), getSerMatricula());
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<AihVO> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty, final boolean asc) {
		List<AihVO> retorno = new ArrayList<AihVO>();
		List<FatAih> lista = faturamentoFacade.pesquisarAihsLiberadas(firstResult, maxResult, orderProperty, asc, getFatAih().getNroAih(), getCthSeq(),
				obterDominios(getFatAih().getIndSituacao()), getFatAih().getDthrEmissao(), getSerVinCodigo(), getSerMatricula());
		if (lista != null && !lista.isEmpty()) {
			for (FatAih fatAih : lista) {
				AihVO item = new AihVO();
				item.setFatAih(fatAih);
				if (fatAih.getContaHospitalar() != null) {
					item.setContaHospitalarPac(faturamentoFacade.obterVFatContaHospitalarPac(fatAih.getContaHospitalar().getSeq()));
				}
			}
		}
		return retorno;
	}

	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		init();
		this.dataModel.setPesquisaAtiva(false);
	}

	private List<DominioSituacaoAih> obterDominios(DominioSituacaoAih situacaoAih) {
		List<DominioSituacaoAih> listaDominios;
		if (situacaoAih == null) {
			listaDominios = getSituacoes();
		} else {
			listaDominios = new ArrayList<DominioSituacaoAih>(1);
			listaDominios.add(situacaoAih);
		}
		return listaDominios;
	}

	public List<FatProcedimentosHospitalares> pesquisarTabela(final Object param) {
		try {
			return faturamentoFacade.listarProcedimentosHospitalar(param);
		} catch (final BaseException e) {
			LOG.error("Exceção capturada: ", e);
			return new ArrayList<FatProcedimentosHospitalares>(0);
		}
	}

	public Long pesquisarTabelaCount(final Object param) {
		try {
			return faturamentoFacade.listarProcedimentosHospitalarCount(param);
		} catch (final BaseException e) {
			LOG.error("Exceção capturada: ", e);
			return 0L;
		}
	}

	public void setFatAih(FatAih fatAih) {
		this.fatAih = fatAih;
	}

	public FatAih getFatAih() {
		return fatAih;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setSerMatricula(Integer serMatricula) {
		this.serMatricula = serMatricula;
	}

	public Integer getSerMatricula() {
		return serMatricula;
	}

	public void setSerVinCodigo(Short serVinCodigo) {
		this.serVinCodigo = serVinCodigo;
	}

	public Short getSerVinCodigo() {
		return serVinCodigo;
	}

	public void setSituacoes(List<DominioSituacaoAih> situacoes) {
		this.situacoes = situacoes;
	}

	public List<DominioSituacaoAih> getSituacoes() {
		return situacoes;
	}

	public DynamicDataModel<AihVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<AihVO> dataModel) {
		this.dataModel = dataModel;
	}

}
