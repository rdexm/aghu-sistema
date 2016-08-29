package br.gov.mec.aghu.faturamento.action;

import java.math.BigDecimal;
import java.text.DecimalFormat;
import java.text.NumberFormat;
import java.util.List;
import java.util.Locale;

import javax.annotation.PostConstruct;
import javax.ejb.EJB;
import javax.inject.Inject;

import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.FatAtoMedicoAih;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatEspelhoAih;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.action.ActionPaginator;
import br.gov.mec.aghu.core.etc.DynamicDataModel;
import br.gov.mec.aghu.core.etc.Paginator;

public class ConsultarEspelhoAIHPaginatorController extends ActionController implements ActionPaginator {

	/**
	 * 
	 */
	private static final long serialVersionUID = 6406204289594517081L;
	public static final Locale LOCALE = new Locale("pt", "BR");
	private static final NumberFormat FORMATTER = new DecimalFormat("######0.##");
	private Integer seq;
	private boolean inicial = true;
	private FatContasHospitalares fatContasHospitalares;
	private FatEspelhoAih espelhoAih;
	private BigDecimal vlTotal = BigDecimal.ZERO;
	private String origem;
	
	// Dados do Paciente
	private Integer pacCodigo;
	private String pacNome;
	private Integer pacProntuario;

	@Inject @Paginator
	private DynamicDataModel<FatAtoMedicoAih> dataModel;
	
	@EJB
	private IFaturamentoFacade faturamentoFacade;

	public static enum RetornoAcaoStrEnum {

		MANTER_CONTA_HOSPITALAR("manterContaHospitalar"), CONSULTAR_CONTA_HOSPITALAR("consultarContaHospitalar"), ENCERRAMENTO_PREVIA_CONTA(
		"encerramentoPreviaConta");

		private final String str;

		RetornoAcaoStrEnum(final String str) {
			this.str = str;
		}

		@Override
		public String toString() {

			return this.str;
		}

	}

	@PostConstruct
	public void init() {
		begin(conversation);
	}
	
	public void inicio() {
	 
        vlTotal = BigDecimal.ZERO;
		if (isInicial() || origem != null) {
			if (getSeq() != null) {
				setFatContasHospitalares(faturamentoFacade.obterContaHospitalar(getSeq()));
			}
			if (getFatContasHospitalares() == null) {
				this.dataModel.setPesquisaAtiva(false);
			} else {
				// Busca o Espelho 1, que é o único que tem dados.
				List<FatEspelhoAih> espelhos = faturamentoFacade.obterFatEspelhoAihPorCth(getFatContasHospitalares().getSeq());
				if(espelhos != null){
					for (final FatEspelhoAih espelhoAih : espelhos) {
						if (getEspelhoAih() == null || espelhoAih.getId().getSeqp() < getEspelhoAih().getId().getSeqp()) {
							setEspelhoAih(espelhoAih);
						}
					}
				}
				// carrega valor Total
				// :CTH1.VALOR_SH+ :CTH1.VALOR_UTI + :CTH1.VALOR_UTIE +
				// :CTH1.VALOR_SP + :CTH1.VALOR_ACOMP + :CTH1.VALOR_RN +
				// :CTH1.VALOR_SADT + :CTH1.VALOR_HEMAT + :CTH1.VALOR_TRANSP +
				// :CTH1.VALOR_OPM + :CTH1.VALOR_ANESTESISTA +
				// :CTH1.VALOR_PROCEDIMENTO
				setVlTotal(somarTotal(getVlTotal(), getFatContasHospitalares().getValorSh(), getFatContasHospitalares().getValorUti(),
						getFatContasHospitalares().getValorUtie(), getFatContasHospitalares().getValorSp(), getFatContasHospitalares()
						.getValorAcomp(), getFatContasHospitalares().getValorRn(), getFatContasHospitalares().getValorSadt(),
						getFatContasHospitalares().getValorHemat(), getFatContasHospitalares().getValorTransp(), getFatContasHospitalares()
						.getValorOpm(), getFatContasHospitalares().getValorAnestesista(), getFatContasHospitalares().getValorProcedimento()));
				this.dataModel.reiniciarPaginator();
			}
			setInicial(false);
		}
	
	}

	private BigDecimal somarTotal(BigDecimal origem, final BigDecimal... adicionado) {
		if (adicionado != null && adicionado.length > 0) {
			for (final BigDecimal add : adicionado) {
				if (add != null) {
					origem = origem.add(add);
				}
			}
		}
		return origem;
	}

	@Override
	public Long recuperarCount() {
		return this.faturamentoFacade.listarFatAtoMedicoEspelhoCount(getSeq());
	}

	@Override
	public List<FatAtoMedicoAih> recuperarListaPaginada(final Integer firstResult, final Integer maxResult, final String orderProperty,
			final boolean asc) {
		final List<FatAtoMedicoAih> lista = faturamentoFacade.buscarAtosMedicosEspelho(getSeq(), firstResult, maxResult, orderProperty, asc);
		if (lista == null || lista.isEmpty()) {
			this.dataModel.setPesquisaAtiva(false);
			//return new ArrayList<FatAtoMedicoAih>(0);
		}
		return lista;
	}

	public String voltar() {
		inicial = false;
		return origem;
	}

	public void setFatContasHospitalares(final FatContasHospitalares fatContasHospitalares) {
		this.fatContasHospitalares = fatContasHospitalares;
	}

	public FatContasHospitalares getFatContasHospitalares() {
		return fatContasHospitalares;
	}

	public void setSeq(final Integer seq) {
		this.seq = seq;
	}

	public Integer getSeq() {
		return seq;
	}

	public void setInicial(final boolean inicial) {
		this.inicial = inicial;
	}

	public boolean isInicial() {
		return inicial;
	}

	public void setVlTotal(final BigDecimal vlTotal) {
		this.vlTotal = vlTotal;
	}
	
	public String getVlTotalFormatado() {
		return FORMATTER.format(vlTotal);   
	}

	public BigDecimal getVlTotal() {
		return vlTotal;
	}

	public void setEspelhoAih(final FatEspelhoAih espelhoAih) {
		this.espelhoAih = espelhoAih;
	}

	public FatEspelhoAih getEspelhoAih() {
		return espelhoAih;
	}

	public void setOrigem(final String origem) {
		this.origem = origem;
	}

	public String getOrigem() {
		return origem;
	}

	public Integer getPacCodigo() {
		return pacCodigo;
	}

	public void setPacCodigo(Integer pacCodigo) {
		this.pacCodigo = pacCodigo;
	}

	public String getPacNome() {
		return pacNome;
	}

	public void setPacNome(String pacNome) {
		this.pacNome = pacNome;
	}

	public Integer getPacProntuario() {
		return pacProntuario;
	}

	public void setPacProntuario(Integer pacProntuario) {
		this.pacProntuario = pacProntuario;
	}

	public DynamicDataModel<FatAtoMedicoAih> getDataModel() {
		return dataModel;
	}

	public void setDataModel(DynamicDataModel<FatAtoMedicoAih> dataModel) {
		this.dataModel = dataModel;
	}
}
