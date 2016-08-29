package br.gov.mec.aghu.faturamento.business;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.faturamento.dao.FatEspelhoAihDAO;
import br.gov.mec.aghu.faturamento.dao.FatItensProcedHospitalarDAO;
import br.gov.mec.aghu.faturamento.dao.FatProcedimentoRegistroDAO;
import br.gov.mec.aghu.faturamento.vo.ResumoCobrancaAihVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatCaractFinanciamento;
import br.gov.mec.aghu.model.FatContasHospitalares;
import br.gov.mec.aghu.model.FatProcedimentoRegistro;
import br.gov.mec.aghu.model.FatProcedimentoRegistroId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;

@Stateless
public class RelatorioResumoCobrancaAihRN extends BaseBusiness implements Serializable {

	private static final Log LOG = LogFactory.getLog(RelatorioResumoCobrancaAihRN.class);

	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}

	@EJB
	private IFaturamentoFacade iFaturamentoFacade;

	@Inject
	private FatItensProcedHospitalarDAO fatItensProcedHospitalarDAO;

	@EJB
	private IPacienteFacade iPacienteFacade;

	@Inject
	private FatProcedimentoRegistroDAO fatProcedimentoRegistroDAO;

	@EJB
	private IAghuFacade iAghuFacade;

	@Inject
	private FatEspelhoAihDAO fatEspelhoAihDAO;

	private static final long serialVersionUID = -943076561873576007L;

	/**
	 * @ORADB FATC_BUSCA_INSTR_REG
	 * 
	 */
	public Boolean buscaInstrRegistro(final Integer iphSeq, final Short iphPhoSeq, final String codRegistro) {

		FatProcedimentoRegistro procedimentoRegistro = getFatProcedimentoRegistroDAO().obterPorChavePrimaria(new FatProcedimentoRegistroId(iphPhoSeq, iphSeq, codRegistro));

		if (procedimentoRegistro != null) {
			return Boolean.TRUE;
		}

		return Boolean.FALSE;
	}

	/**
	 * @ORADB FATC_BUSCA_PROCED_PR_CTA
	 * 
	 */
	public String buscaProcedimentoPrConta(final Integer seq, final Short phoSeq, final Integer eaiCthSeq, final Long codTabela) {

		Long codigoTabela = getFatItensProcedHospitalarDAO().obterCodTabelaAtoMedico(seq, phoSeq, eaiCthSeq, codTabela);

		if (codigoTabela != null) {
			return StringUtils.leftPad(codigoTabela.toString(), 10, "0") + "2";
		}

		return StringUtils.leftPad(codTabela.toString(), 10, "0") + "1";
	}

	private void obterServicosProfissionais(final Boolean previa, ResumoCobrancaAihVO vo) {

		if (!previa) {

			if (vo.getIndImprimeEspelho() != null && vo.getIndImprimeEspelho()) {
				vo.setReimpressao(super.getResourceBundleValue("REIMPRESSAO"));
			}

			vo.setListaServicos(getFaturamentoFacade().listarAtosMedicos(vo.getSeqp(), vo.getCthSeq()));

		} else {
			vo.setListaServicos(getFaturamentoFacade().listarAtosMedicosPrevia(vo.getSeqp(), vo.getCthSeq()));
		}
	}

	private void formatarRelatorioResumoCobrancaAih(final Boolean previa, List<ResumoCobrancaAihVO> resumoCobrancaAihVOs) {

		for (ResumoCobrancaAihVO vo : resumoCobrancaAihVOs) {

			FatCaractFinanciamento item = getFaturamentoFacade().obterCaractFinanciamentoPorSeqEPhoSeqECodTabela(vo.getPhoSeqRealz(), vo.getSeqRealz(), vo.getIphCodSusRealz());

			vo.setDetalhe1((item != null) ? item.getDescricao() : " ");

			formatarProntuario(vo);

			formatarDetalhe2(vo);

			formatarDetalhe3(vo);

			formatarEspecialidade(vo);

			formatarDataApresentacao(vo);

			formatarDv(vo);

			formatarDataHoraCriadoEmDtPrevia(vo);

			formatarCpfCnsMedicoAuditor(vo);

			obterServicosProfissionais(previa, vo);
		}
	}

	private void formatarCpfCnsMedicoAuditor(ResumoCobrancaAihVO vo) {
		try {
			Calendar dataComparacao = Calendar.getInstance();
			dataComparacao.setTime(new SimpleDateFormat("yyyyMM").parse("201112"));

			Calendar dataInternacao = Calendar.getInstance();
			dataInternacao.setTime(new SimpleDateFormat("yyyyMM").parse(vo.getDtInternacao().toString()));

			if (dataComparacao.after(dataInternacao)) {
				if (vo.getCnsMedicoAuditor() != null) {
					vo.setCpfOuCnsMedicoAuditor(vo.getCpfMedicoAuditorFormatado());
				}
			} else {
				if (vo.getCpfMedicoAuditor() != null) {
					vo.setCpfOuCnsMedicoAuditor(vo.getCnsMedicoAuditor().toString());
				}
			}
		} catch (ParseException e) {
			this.logError(e.getMessage());
		}
	}

	private void formatarDataHoraCriadoEmDtPrevia(ResumoCobrancaAihVO vo) {
		SimpleDateFormat dateFormat = new SimpleDateFormat("dd/MM/yyyy HH:mm");

		if (vo.getCriadoEm() != null && vo.getDtPrevia() != null) {
			vo.setStrCriadoEm(dateFormat.format(vo.getCriadoEm()));
			vo.setStrDtPrevia(dateFormat.format(vo.getDtPrevia()));
		}
	}

	private void formatarDataApresentacao(ResumoCobrancaAihVO vo) {
		if (vo.getDciCpeMes() != null && vo.getDciCpeAno() != null) {
			try {
				SimpleDateFormat dateFormat = new SimpleDateFormat("MMyyyy");

				Calendar dataApresentacao = Calendar.getInstance();
				dataApresentacao.setTime(dateFormat.parse(StringUtils.leftPad(vo.getDciCpeMes().toString(), 2, "0") + vo.getDciCpeAno().toString()));
				dataApresentacao.add(Calendar.MONTH, 1);

				vo.setDtApresentacao(dateFormat.format(dataApresentacao.getTime()));
			} catch (ParseException e) {
				this.logError(e.getMessage());
			}
		}
	}

	private void formatarEspecialidade(ResumoCobrancaAihVO vo) {
		if (vo.getEspecialidadeAih() != null) {
			AghClinicas clinica = getAghuFacade().obterClinica(Integer.valueOf(vo.getEspecialidadeAih()));
			vo.setEspecialidade(clinica.getDescricao());
		}
	}

	private void formatarDetalhe3(ResumoCobrancaAihVO vo) {
		FatContasHospitalares conta = getFaturamentoFacade().obterContaHospitalar(vo.getCthSeq());
		vo.setDetalhe3((conta.getEspecialidade() != null) ? conta.getEspecialidade().getNomeReduzido() : " ");
	}

	private void formatarDetalhe2(ResumoCobrancaAihVO vo) {
		if (vo.getCthSeqReapresentada() != null) {
			FatContasHospitalares contaReapr = getFaturamentoFacade().obterContaHospitalar(vo.getCthSeqReapresentada());
			vo.setDetalhe2((contaReapr.getMotivoRejeicao() != null) ? contaReapr.getMotivoRejeicao().getDescricao() : " ");
		}
	}

	private void formatarProntuario(ResumoCobrancaAihVO vo) {
		if (vo.getProntuario() != null) {
			AipPacientes paciente = getPacienteFacade().obterPacientePorProntuario(vo.getProntuario());
			vo.setCodPaciente(paciente.getCodigo());
			String prontuarioBarcode = formataProntuarioBarcode(paciente.getProntuario());
			vo.setProntuarioFormatado(prontuarioBarcode);
			if (paciente.getNroCartaoSaude() != null) {
				vo.setNroCartaoSaude(paciente.getNroCartaoSaude().toString());
			}
		}
	}
	
	/**
	 * Formata o prontuário da paciente para ficar com 12 digitos complementando com zeros
	 * function CF_BARRASFormula 
	 * @param prontuarioPaciente
	 * @return
	 */
	public String formataProntuarioBarcode(Integer prontuarioPaciente){
		String prontFormatado = StringUtils.leftPad(String.valueOf(prontuarioPaciente), 9, '0');
		prontFormatado = StringUtils.rightPad(prontFormatado, 12, '0');
		return prontFormatado;
	}

	private List<ResumoCobrancaAihVO> pesquisarEspelhoAih(final Integer cthSeq) {
		return getFatEspelhoAihDAO().pesquisarEspelhoAih(cthSeq);
	}

	public List<ResumoCobrancaAihVO> gerarRelatorioResumoCobrancaAih(final Integer cthSeq, final Boolean previa) {
		List<ResumoCobrancaAihVO> resumoCobrancaAihVOs = pesquisarEspelhoAih(cthSeq);
		formatarRelatorioResumoCobrancaAih(previa, resumoCobrancaAihVOs);
		return resumoCobrancaAihVOs;
	}

	private void formatarDv(ResumoCobrancaAihVO vo) {
		if (vo.getDciCpeAno() != null) {

			String modulo = vo.getDciCpeAno() + "02" + StringUtils.leftPad(((vo.getCthSeq() != null) ? vo.getCthSeq().toString() : "0"), 6, "0");

			Integer dv = CoreUtil.calculaModuloOnze(Long.valueOf(modulo));
			vo.setDv(dv);

		} else {
			SimpleDateFormat sdf = new SimpleDateFormat("yyyy");
			Date dataAtual = new Date();
			String modulo = sdf.format(dataAtual) + "02" + StringUtils.leftPad(((vo.getCthSeq() != null) ? vo.getCthSeq().toString() : "0"), 6, "0");

			Integer dv = CoreUtil.calculaModuloOnze(Long.valueOf(modulo));
			vo.setDv(dv);
		}
	}

	protected FatProcedimentoRegistroDAO getFatProcedimentoRegistroDAO() {
		return fatProcedimentoRegistroDAO;
	}

	protected FatItensProcedHospitalarDAO getFatItensProcedHospitalarDAO() {
		return fatItensProcedHospitalarDAO;
	}

	protected FatEspelhoAihDAO getFatEspelhoAihDAO() {
		return fatEspelhoAihDAO;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return iFaturamentoFacade;
	}

	protected IPacienteFacade getPacienteFacade() {
		return iPacienteFacade;
	}

	protected IAghuFacade getAghuFacade() {
		return iAghuFacade;
	}

}
