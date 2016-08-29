package br.gov.mec.aghu.faturamento.action;

import java.util.List;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioTipoNutricaoParenteral;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatProcedHospInternos;
import br.gov.mec.aghu.model.MbcProcedimentoCirurgicos;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;


public class ManterProcedimentoHospitalarInternoPaginatorController extends ActionController implements ActionPaginator  {

	/**
	 * 
	 */
	private static final long serialVersionUID = 7880150833708412771L;

	private final String REDIRECIONA_CADASTRAR_PHI = "manterProcedimentoHospitalarInterno";
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	// FILTROS
	private FatProcedHospInternos procedimentoHospitalar;
	private MbcProcedimentoCirurgicos procedimentoCirurgico;
	private FatProcedHospInternos procedimentoAgrupado;

	// Parametros
	private String origem;

	private Boolean somenteAtivo = false;
	private Boolean origemPrescricao;
	private DominioTipoNutricaoParenteral tipoNutricao;
	
	private boolean inicial = true;
	
	@Inject @Paginator
	private DynamicDataModel<FatProcedHospInternos> dataModel;
	
	public enum ManteProcedimentoHospitalarInternoPaginatorControllerExceptionCode implements BusinessExceptionCode {
		LABEL_INFORME_UM_CAMPO_PARA_PESQUISAR
	}
	
	@SuppressWarnings("PMD.UnusedPrivateMethod")
	@PostConstruct
	private void init(){
		begin(conversation);
	}

    public void inicio() {
        if (isInicial()) {
            setInicial(false);

            somenteAtivo = true;
        }
    }

	public void pesquisar() {
		dataModel.reiniciarPaginator();
	}

	public void limparPesquisa() {
		somenteAtivo = true;
		origemPrescricao = false;
		procedimentoHospitalar = null;
		procedimentoCirurgico = null;
		procedimentoAgrupado = null;
		tipoNutricao = null;
		dataModel.limparPesquisa();
	}

	public String iniciarInclusao() {
		return REDIRECIONA_CADASTRAR_PHI;
	}
	
	public String editar() {
		return REDIRECIONA_CADASTRAR_PHI;
	}

	@Override
	public Long recuperarCount() {
		DominioSituacao situacao;
		if (somenteAtivo) {
			situacao = DominioSituacao.A;
		} else {
			situacao = DominioSituacao.I;
		}
		
		Integer pciSeq = null;
		if (procedimentoCirurgico != null) {
			pciSeq = procedimentoCirurgico.getSeq();
		}
		
		Integer phiSeq = null;
		if (procedimentoHospitalar != null) {
			phiSeq = procedimentoHospitalar.getSeq();
		}
		
		Integer phiSeqAgrupado = null;
		if (procedimentoAgrupado != null) {
			phiSeqAgrupado = procedimentoAgrupado.getSeq();
		}
		
		return this.faturamentoFacade.listarFatProcedHospInternosCount(situacao, origemPrescricao, tipoNutricao, pciSeq, phiSeq, phiSeqAgrupado);
	}

	@Override
	public List<FatProcedHospInternos> recuperarListaPaginada(Integer firstResult, Integer maxResult, String orderProperty, boolean asc) {

		DominioSituacao situacao;
		if (somenteAtivo) {
			situacao = DominioSituacao.A;
		} else {
			situacao = DominioSituacao.I;
		}
		
		Integer pciSeq = null;
		if (procedimentoCirurgico != null) {
			pciSeq = procedimentoCirurgico.getSeq();
		}
		
		Integer phiSeq = null;
		if (procedimentoHospitalar != null) {
			phiSeq = procedimentoHospitalar.getSeq();
		}
		
		Integer phiSeqAgrupado = null;
		if (procedimentoAgrupado != null) {
			phiSeqAgrupado = procedimentoAgrupado.getSeq();
		}
		
		return this.faturamentoFacade.listarFatProcedHospInternos(firstResult, maxResult, orderProperty, asc, situacao, origemPrescricao,
				tipoNutricao, pciSeq, phiSeq, phiSeqAgrupado);
	}

	/**
	 * Método para pesquisar FAT_PROCED_HOSP_INTERNOS na suggestion da tela
	 * 
	 * @return
	 */
	public List<FatProcedHospInternos> listarProcedHospInternoPorSeqOuDescricao(String param) {
		return this.returnSGWithCount(faturamentoFacade.listarProcedHospInternoPorSeqOuDescricao(param, 100,FatProcedHospInternos.Fields.SEQ.toString()),listarProcedHospInternoPorSeqOuDescricaoCount(param));
	}
	
	public Long listarProcedHospInternoPorSeqOuDescricaoCount(String param) {
		return faturamentoFacade.listarProcedHospInternoPorSeqOuDescricaoCount(param);
	}

	/**
	 * Pesquisa para Suggestion procedimentos hospitalares agrupados
	 * 
	 * @param param
	 * @return
	 */
	public List<FatProcedHospInternos> pesquisarProcedimentosHospitalaresAgrupados(String param) {
		return this.returnSGWithCount(faturamentoFacade.listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricao(param,
				FatProcedHospInternos.Fields.DESCRICAO.toString(), DominioSituacao.A),pesquisarProcedimentosHospitalaresAgrupadosCount(param));
	}

	public Long pesquisarProcedimentosHospitalaresAgrupadosCount(String param) {
		return faturamentoFacade.listarProcedimentosHospitalaresAgrupadosPorPhiSeqOuDescricaoCount(param, DominioSituacao.A);
	}

	/**
	 * Pesquisa para Suggestion procedimentos cirurgicos
	 * 
	 * @param param
	 * @return
	 */
	public List<MbcProcedimentoCirurgicos> pesquisarProcedimentosCirurgicos(String param) {
		return this.returnSGWithCount(blocoCirurgicoFacade.pesquisarProcedimentosCirurgicos(param, MbcProcedimentoCirurgicos.Fields.SEQ.toString(), 100, DominioSituacao.A),pesquisarProcedimentosCirurgicosCount(param));
	}

	public Long pesquisarProcedimentosCirurgicosCount(String param) {
		return blocoCirurgicoFacade.pesquisarProcedimentosCirurgicosCount(param, DominioSituacao.A);
	}

	public String voltar() {
        limparPesquisa();
		return origem;
	}

	/************* GETTERS AND SETTERS ******************/
	public FatProcedHospInternos getProcedimentoHospitalar() {
		return procedimentoHospitalar;
	}

	public void setProcedimentoHospitalar(
			FatProcedHospInternos procedimentoHospitalar) {
		this.procedimentoHospitalar = procedimentoHospitalar;
	}

	public MbcProcedimentoCirurgicos getProcedimentoCirurgico() {
		return procedimentoCirurgico;
	}

	public void setProcedimentoCirurgico(MbcProcedimentoCirurgicos procedimentoCirurgico) {
		this.procedimentoCirurgico = procedimentoCirurgico;
	}

	public FatProcedHospInternos getProcedimentoAgrupado() {
		return procedimentoAgrupado;
	}

	public void setProcedimentoAgrupado(FatProcedHospInternos procedimentoAgrupado) {
		this.procedimentoAgrupado = procedimentoAgrupado;
	}

	public Boolean getOrigemPrescricao() {
		return origemPrescricao;
	}

	public void setOrigemPrescricao(Boolean origemPrescricao) {
		this.origemPrescricao = origemPrescricao;
	}

	public DominioTipoNutricaoParenteral getTipoNutricao() {
		return tipoNutricao;
	}

	public void setTipoNutricao(DominioTipoNutricaoParenteral tipoNutricao) {
		this.tipoNutricao = tipoNutricao;
	}

	public Boolean getSomenteAtivo() {
		return somenteAtivo;
	}

	public void setSomenteAtivo(Boolean somenteAtivo) {
		this.somenteAtivo = somenteAtivo;
	}

	public String getOrigem() {
		return origem;
	}

	public void setOrigem(String origem) {
		this.origem = origem;
	}
	
	public void setInicial(boolean inicial) {
		this.inicial = inicial;
	}

	public boolean isInicial() {
		return inicial;
	}

	public DynamicDataModel<FatProcedHospInternos> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatProcedHospInternos> dataModel) {
		this.dataModel = dataModel;
	}
}