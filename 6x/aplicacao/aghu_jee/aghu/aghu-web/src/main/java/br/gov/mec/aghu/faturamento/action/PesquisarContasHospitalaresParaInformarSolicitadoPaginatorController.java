package br.gov.mec.aghu.faturamento.action;

import java.util.ArrayList;
import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.aghparametros.business.IParametroFacade;
import br.gov.mec.aghu.aghparametros.util.AghuParametrosEnum;
import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.faturamento.vo.ContaHospitalarInformarSolicitadoVO;
import br.gov.mec.aghu.model.VFatContasHospPacientes;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.Severity;

/**
 * @author aghu
 *
 */
public class PesquisarContasHospitalaresParaInformarSolicitadoPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7826860122009593613L;
	
	@Inject @Paginator
	private DynamicDataModel<ContaHospitalarInformarSolicitadoVO> dataModel;
	
	private final String PAGE_INFORMAR_SOLICITADO_HC = "informarSolicitadoContaHospitalar";
	//private final String PAGE_FROM = "pesquisarContasHospitalaresParaInformarSolicitado";

	@EJB
	private IParametroFacade parametroFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private Integer pacProntuario;

	private Integer pacCodigo;

	private Integer cthSeq;

	private Long cthNroAih;
	
	private Byte seqTipoAih;

	private DominioSituacaoConta situacao;
	
	private List<DominioSituacaoConta> situacoes;
	
	@PostConstruct
	public void init() {
		begin(conversation);
		
		try {
			situacoes = new ArrayList<DominioSituacaoConta>();
			situacoes.add(DominioSituacaoConta.A);
			situacoes.add(DominioSituacaoConta.F);
			
			seqTipoAih = parametroFacade.buscarAghParametro(AghuParametrosEnum.P_TIPO_AIH_LONGA_PERMANENCIA).getVlrNumerico().byteValue();
		} catch (ApplicationBusinessException e) {
			this.apresentarMsgNegocio(Severity.INFO, "MENSAGEM_ERRO_CARREGAR_TIPO_AIH_5");
		}
	}
	
	@Override
	public Long recuperarCount() {
		DominioSituacaoConta[] situacoesParam = null;
		if (this.situacao != null) {
			situacoesParam = new DominioSituacaoConta[] { this.situacao };
		} else {
			situacoesParam = new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F }; 
		}
		
		return this.faturamentoFacade.pesquisarContaHospitalarInformarSolicitadoVOCount(this.pacProntuario, this.cthNroAih, this.pacCodigo, this.cthSeq,
				situacoesParam, this.seqTipoAih);
	}

	@SuppressWarnings("unchecked")
	@Override
	public List<ContaHospitalarInformarSolicitadoVO> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty,
			boolean asc) {
		DominioSituacaoConta[] situacoesParam = null;
		if (this.situacao != null) {
			situacoesParam = new DominioSituacaoConta[] { this.situacao };
		} else {
			situacoesParam = new DominioSituacaoConta[] { DominioSituacaoConta.A, DominioSituacaoConta.F }; 
		}		
		
		return this.faturamentoFacade.pesquisarContaHospitalarInformarSolicitadoVO(this.pacProntuario, this.cthNroAih, this.pacCodigo, this.cthSeq,
				situacoesParam, this.seqTipoAih, firstResult, maxResult, VFatContasHospPacientes.Fields.PAC_PRONTUARIO.toString(), true);
	}
	
	/**
	 * Método executado ao clicar no botão limpar
	 */
	public void limparPesquisa() {
		this.pacProntuario = null;
		this.pacCodigo = null;
		this.cthSeq = null;
		this.cthNroAih = null;
		this.situacao = null;
		this.dataModel.limparPesquisa();
	}
	
	public void pesquisar() {
		this.dataModel.reiniciarPaginator();
	}
	
	public String editar() {
		return PAGE_INFORMAR_SOLICITADO_HC;
	}
	
	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public Integer getCthSeq() {
		return cthSeq;
	}

	public void setCthSeq(Integer cthSeq) {
		this.cthSeq = cthSeq;
	}

	public Long getCthNroAih() {
		return cthNroAih;
	}

	public void setCthNroAih(Long cthNroAih) {
		this.cthNroAih = cthNroAih;
	}

	public DominioSituacaoConta getSituacao() {
		return situacao;
	}

	public void setSituacao(DominioSituacaoConta situacao) {
		this.situacao = situacao;
	}

	public List<DominioSituacaoConta> getSituacoes() {
		return situacoes;
	}

	public void setSituacoes(List<DominioSituacaoConta> situacoes) {
		this.situacoes = situacoes;
	}

	public DynamicDataModel<ContaHospitalarInformarSolicitadoVO> getDataModel() {
		return dataModel;
	}

	public void setDataModel(
			DynamicDataModel<ContaHospitalarInformarSolicitadoVO> dataModel) {
		this.dataModel = dataModel;
	}

}
