package br.gov.mec.aghu.internacao.action;

import java.text.SimpleDateFormat;
import java.util.HashSet;
import java.util.List;

import javax.ejb.EJB;

import br.gov.mec.aghu.dominio.DominioSituacaoConta;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.internacao.business.IInternacaoFacade;
import br.gov.mec.aghu.internacao.cadastrosbasicos.business.ICadastrosBasicosInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.ConvenioPlanoVO;
import br.gov.mec.aghu.model.AinAcomodacoes;
import br.gov.mec.aghu.model.AinInternacao;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatContasInternacao;
import br.gov.mec.aghu.core.action.ActionController;
import br.gov.mec.aghu.core.exception.Severity;

public class AtualizarAcomodacaoController extends ActionController {

	/**
	 * 
	 */
	private static final long serialVersionUID = -5214735026153614654L;
	
	private final String PAGE_CADASTRO_INTERNACAO = "cadastroInternacao";

	@EJB
	private IInternacaoFacade internacaoFacade;

	@EJB
	private ICadastrosBasicosInternacaoFacade cadastrosBasicosInternacaoFacade;

	@EJB
	private IFaturamentoFacade faturamentoFacade;

	private AinInternacao internacao;

	private AipPacientes paciente;

	private Short aghUniFuncSeq;

	private String ainLeitoId;

	private String ainQuartoDescricao;

	private Integer seqContaHosp;

	private String strDataIntAdministrativa;

	private FatContasHospitalares contaHospitalar;

	/* Acomodação Autorizada */
	private AinAcomodacoes acomodacaoPesq;

	private ConvenioPlanoVO convenioPlanoVO;

	/**
	 * Seq da Internação, obtido via page parameter.
	 */
	private Integer ainInternacaoSeq;

	/**
	 * Código do Paciente, obtido via page parameter.
	 */
	private Integer aipPacCodigo;

	public void iniciar() {
	 


		internacao = internacaoFacade.obterAinInternacaoPorChavePrimaria(ainInternacaoSeq, AinInternacao.Fields.PACIENTE, AinInternacao.Fields.FAT_CONTAS_INTERNACAO);
		paciente = internacao.getPaciente();

		convenioPlanoVO = internacaoFacade.obterConvenioPlanoVO(internacao.getConvenioSaudePlano().getId().getCnvCodigo(), internacao.getConvenioSaudePlano().getId().getSeq());

		if (internacao.getLeito() != null) {
			ainLeitoId = internacao.getLeito().getLeitoID();
		} else if (internacao.getQuarto() != null) {
			internacao.setQuarto(internacaoFacade.obterAinQuartosPorChavePrimaria(internacao.getQuarto().getNumero()));
			ainQuartoDescricao = internacao.getQuarto().getDescricao();
		} else {
			aghUniFuncSeq = internacao.getUnidadesFuncionais().getSeq();
		}

		final List<FatContasInternacao> listaContasInternacao = faturamentoFacade.pesquisarContasInternacaoOrderDtInternacaoDesc(internacao.getSeq());

		// É sempre utilizada a conta internação cuja conta hospitalar estiver
		// Aberta
		for (final FatContasInternacao conta : listaContasInternacao) {
			FatContasInternacao contaAux = faturamentoFacade.obterFatContasInternacaoPorChavePrimaria(conta.getSeq(), FatContasInternacao.Fields.FAT_CONTAS_HOSPITALARES);

			if (contaAux.getContaHospitalar() != null && DominioSituacaoConta.A.equals(contaAux.getContaHospitalar().getIndSituacao())) {

				contaHospitalar = contaAux.getContaHospitalar();
				
				contaHospitalar = faturamentoFacade.obterContaHospitalar(contaHospitalar.getSeq(), FatContasHospitalares.Fields.ACOMODACAO);

				if (contaHospitalar.getAcomodacao() != null) {
					acomodacaoPesq = contaHospitalar.getAcomodacao();
				}
				if (contaHospitalar != null) {
					seqContaHosp = contaHospitalar.getSeq();
					final SimpleDateFormat df = new SimpleDateFormat("dd/MM/yyyy");
					strDataIntAdministrativa = df.format(contaHospitalar.getDataInternacaoAdministrativa());
				}

				List<FatContasInternacao> fatContasInternacao = faturamentoFacade.listarContasInternacao(internacao.getSeq());
				internacao.setFatContasInternacao(new HashSet<FatContasInternacao>(fatContasInternacao));
				internacao.addFatContaInternacao(contaAux);
				break;
			}
		}
	
	}

	public List<AinAcomodacoes> pesquisarAcomodacoes(String objParam) {
		List<AinAcomodacoes> retorno;

		retorno = cadastrosBasicosInternacaoFacade.pesquisarAcomodacoesPorCodigoOuDescricaoOrdenado(objParam);

		return retorno;

	}

	/**
	 * Método que realiza a ação do botão confirmar na tela de atualização de
	 * acomodação autorizada.
	 */
	public String confirmar() {

		// try {
		if (contaHospitalar != null) {
			contaHospitalar.setAcomodacao(acomodacaoPesq);
			internacaoFacade.atualizarContaHospitalar(contaHospitalar);
		}

		apresentarMsgNegocio(Severity.INFO, "MENSAGEM_SUCESSO_ACOMODACAO_SALVA");

		this.acomodacaoPesq = null;

		return PAGE_CADASTRO_INTERNACAO;
		/*
		 * } catch (final InactiveModuleException e) {
		 * getLog().error(e.getMessage());
		 * this.getStatusMessages().add(Severity.ERROR, e.getMessage()); } catch
		 * (final OptimisticLockException e) { getLog().error(e.getMessage(),
		 * e); throw e; }
		 */
		// return null;
	}

	/**
	 * Método que realiza a ação do botão cancelar na tela de atualização de
	 * acomodação autorizada.
	 */
	public String cancelar() {
		this.acomodacaoPesq = null;
		return PAGE_CADASTRO_INTERNACAO;
	}

	// Getters and Setters
	public AinInternacao getInternacao() {
		return internacao;
	}

	public void setInternacao(final AinInternacao internacao) {
		this.internacao = internacao;
	}

	public Integer getAinInternacaoSeq() {
		return ainInternacaoSeq;
	}

	public void setAinInternacaoSeq(final Integer ainInternacaoSeq) {
		this.ainInternacaoSeq = ainInternacaoSeq;
	}

	public Integer getAipPacCodigo() {
		return aipPacCodigo;
	}

	public void setAipPacCodigo(final Integer aipPacCodigo) {
		this.aipPacCodigo = aipPacCodigo;
	}

	public AipPacientes getPaciente() {
		return paciente;
	}

	public void setPaciente(final AipPacientes paciente) {
		this.paciente = paciente;
	}

	public String getStyleProntuario() {
		String retorno = "";

		if (this.paciente != null && paciente.isProntuarioVirtual()) {
			retorno = "background-color:#0000ff";
		}
		return retorno;

	}

	public Short getAghUniFuncSeq() {
		return aghUniFuncSeq;
	}

	public void setAghUniFuncSeq(final Short aghUniFuncSeq) {
		this.aghUniFuncSeq = aghUniFuncSeq;
	}

	public String getAinLeitoId() {
		return ainLeitoId;
	}

	public void setAinLeitoId(final String ainLeitoId) {
		this.ainLeitoId = ainLeitoId;
	}

	public Integer getSeqContaHosp() {
		return seqContaHosp;
	}

	public void setSeqContaHosp(final Integer seqContaHosp) {
		this.seqContaHosp = seqContaHosp;
	}

	public String getStrDataIntAdministrativa() {
		return strDataIntAdministrativa;
	}

	public void setStrDataIntAdministrativa(final String strDataIntAdministrativa) {
		this.strDataIntAdministrativa = strDataIntAdministrativa;
	}

	public AinAcomodacoes getAcomodacaoPesq() {
		return acomodacaoPesq;
	}

	public void setAcomodacaoPesq(final AinAcomodacoes acomodacaoPesq) {
		this.acomodacaoPesq = acomodacaoPesq;
	}

	public ConvenioPlanoVO getConvenioPlanoVO() {
		return convenioPlanoVO;
	}

	public void setConvenioPlanoVO(final ConvenioPlanoVO convenioPlanoVO) {
		this.convenioPlanoVO = convenioPlanoVO;
	}

	public FatContasHospitalares getContaHospitalar() {
		return contaHospitalar;
	}

	public void setContaHospitalar(final FatContasHospitalares contaHospitalar) {
		this.contaHospitalar = contaHospitalar;
	}

	public String getAinQuartoDescricao() {
		return ainQuartoDescricao;
	}

	public void setAinQuartoDescricao(String ainQuartoDescricao) {
		this.ainQuartoDescricao = ainQuartoDescricao;
	}

}
