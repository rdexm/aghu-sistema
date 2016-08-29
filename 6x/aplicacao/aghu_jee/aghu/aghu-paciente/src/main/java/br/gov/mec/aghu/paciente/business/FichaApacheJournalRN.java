package br.gov.mec.aghu.paciente.business;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.model.MpmFichaApache;
import br.gov.mec.aghu.model.MpmFichaApacheJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class FichaApacheJournalRN extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(FichaApacheJournalRN.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IServidorLogadoFacade servidorLogadoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;

	/**
	 * 
	 */
	private static final long serialVersionUID = 2760600282770009226L;

	private enum FichaApacheJournalRNExceptionCode implements
			BusinessExceptionCode {
		ERRO_CLONE_OBJ_FICHA_APACHE;
	}

	public void gerarJournalFichaApache(
			DominioOperacoesJournal operacaoJournal,
			MpmFichaApache fichaApache, MpmFichaApache fichaApacheOld)
			throws ApplicationBusinessException {
		if (operacaoJournal == DominioOperacoesJournal.UPD) {
			this.gerarJournalAtualizacao(fichaApache, fichaApacheOld);
		} else if (operacaoJournal == DominioOperacoesJournal.DEL) {
			this.gerarJournalRemocao(fichaApache);
		}
	}

	/**
	 * Método para gerar journal de DELETE
	 * 
	 * @param fichaApache
	 */
	private void gerarJournalRemocao(MpmFichaApache fichaApache)
			throws ApplicationBusinessException {
		MpmFichaApacheJn fichaApacheJournal = this.criarFichaApacheJournal(
				fichaApache, DominioOperacoesJournal.DEL);

		this.getPrescricaoMedicaFacade().inserirMpmFichaApacheJn(fichaApacheJournal);
	}

	/**
	 * Método para gerar journal de UPDATE
	 * 
	 * @param fichaApache
	 * @param fichaApacheOld
	 */
	private void gerarJournalAtualizacao(MpmFichaApache fichaApache,
			MpmFichaApache fichaApacheOld) throws ApplicationBusinessException {

		if (fichaApache == null || fichaApacheOld == null) {
			return;
		}

		if (!fichaApache.getId().getAtdSeq()
				.equals(fichaApacheOld.getId().getAtdSeq())
				|| !fichaApache.getId().getSeqp()
						.equals(fichaApacheOld.getId().getSeqp())
				|| (fichaApache.getEscalaGlasgow() != null
						&& fichaApacheOld.getEscalaGlasgow() != null && (int) fichaApache
						.getEscalaGlasgow().getSeq() != (int) fichaApacheOld
						.getEscalaGlasgow().getSeq())
				|| (fichaApache.getServidor() != null
						&& fichaApacheOld.getServidor() != null && (int) fichaApache
						.getServidor().getId().getMatricula() != (int) fichaApacheOld
						.getServidor().getId().getMatricula())
				|| (fichaApache.getServidor() != null
						&& fichaApacheOld.getServidor() != null && (short) fichaApache
						.getServidor().getId().getVinCodigo() != (short) fichaApacheOld
						.getServidor().getId().getVinCodigo())
				|| (fichaApache.getCriadoEm() != null
						&& fichaApacheOld.getCriadoEm() != null && fichaApache
						.getCriadoEm().compareTo(fichaApacheOld.getCriadoEm()) != 0)
				|| (fichaApache.getPontuacaoApacheTemperatura() != fichaApacheOld
						.getPontuacaoApacheTemperatura())
				|| (fichaApache.getPontuacaoApachePressaoArterialMedia() != fichaApacheOld
						.getPontuacaoApachePressaoArterialMedia())
				|| (fichaApache.getPontuacaoApacheFrequenciaCardiaca() != fichaApacheOld
						.getPontuacaoApacheFrequenciaCardiaca())
				|| (fichaApache.getPontuacaoApacheFrequenciaRespiratoria() != fichaApacheOld
						.getPontuacaoApacheFrequenciaRespiratoria())
				|| (fichaApache.getPontuacaoApachePHArterial() != fichaApacheOld
						.getPontuacaoApachePHArterial())
				|| (fichaApache.getPontuacaoApacheSodioPlasmatico() != fichaApacheOld
						.getPontuacaoApacheSodioPlasmatico())
				|| (fichaApache.getPontuacaoApachePotassioPlasmatico() != fichaApacheOld
						.getPontuacaoApachePotassioPlasmatico())
				|| (fichaApache.getPontuacaoApacheCreatininaSerica() != fichaApacheOld
						.getPontuacaoApacheCreatininaSerica())
				|| (fichaApache.getCreatinaSericaAguda() != fichaApacheOld
						.getCreatinaSericaAguda())
				|| (fichaApache.getPontuacaoApacheHematocrito() != fichaApacheOld
						.getPontuacaoApacheHematocrito())
				|| (fichaApache.getPontuacaoApacheLeucocitometria() != fichaApacheOld
						.getPontuacaoApacheLeucocitometria())
				|| (fichaApache.getPontuacaoApacheIdade() != fichaApacheOld
						.getPontuacaoApacheIdade())
				|| (fichaApache.getClinicoPosOperacaoUrgencia() != fichaApacheOld
						.getClinicoPosOperacaoUrgencia())
				|| (fichaApache.getCirurgiaProgramada() != fichaApacheOld
						.getCirurgiaProgramada())
				|| (fichaApache.getPontuacaoApacheO2Fio2Maior05() != fichaApacheOld
						.getPontuacaoApacheO2Fio2Maior05())
				|| (fichaApache.getPontuacaoApacheO2Fio2Menor05() != fichaApacheOld
						.getPontuacaoApacheO2Fio2Menor05())
				|| (fichaApache.getDthrRealizacao() != null
						&& fichaApacheOld.getDthrRealizacao() != null && fichaApache
						.getDthrRealizacao().compareTo(
								fichaApacheOld.getDthrRealizacao()) != 0)
				|| (fichaApache.getEnvioAviso() != fichaApacheOld
						.getEnvioAviso())
				|| (fichaApache.getSituacaoApache() != fichaApacheOld
						.getSituacaoApache())
				|| (fichaApache.getUnidadeFuncional() != null
						&& fichaApacheOld.getUnidadeFuncional() != null && (short) fichaApache
						.getUnidadeFuncional().getSeq() != (short) fichaApacheOld
						.getUnidadeFuncional().getSeq())
				|| (fichaApache.getDthrIngressoUnidade() != fichaApacheOld
						.getDthrIngressoUnidade() && fichaApache
						.getDthrIngressoUnidade().compareTo(
								fichaApacheOld.getDthrIngressoUnidade()) != 0)
				|| (fichaApache.getPacienteCirurgico() != fichaApacheOld
						.getPacienteCirurgico())
				|| (fichaApache.getCirurgiaExtracorporea() != fichaApacheOld
						.getCirurgiaExtracorporea())
				|| (fichaApache.getSindromeCoronariana() != fichaApacheOld
						.getSindromeCoronariana())
				|| (fichaApache.getChoqueCardiogenico() != fichaApacheOld
						.getChoqueCardiogenico())
				|| (fichaApache.getDoencaFigado() != fichaApacheOld
						.getDoencaFigado())
				|| (fichaApache.getDoencaCoracao() != fichaApacheOld
						.getDoencaCoracao())
				|| (fichaApache.getDoencaPulmao() != fichaApacheOld
						.getDoencaPulmao())
				|| (fichaApache.getDoencaRenal() != fichaApacheOld
						.getDoencaRenal())
				|| (fichaApache.getDoencaImunologica() != fichaApacheOld
						.getDoencaImunologica())
				|| (fichaApache.getPosOperatorioUrgencia() != fichaApacheOld
						.getPosOperatorioUrgencia())) {

			MpmFichaApacheJn fichaApacheJournal = this.criarFichaApacheJournal(
					fichaApacheOld, DominioOperacoesJournal.UPD);
			this.getPrescricaoMedicaFacade().inserirMpmFichaApacheJn(fichaApacheJournal);
		}
	}

	private MpmFichaApacheJn criarFichaApacheJournal(
			MpmFichaApache fichaApache, DominioOperacoesJournal operacao)
			throws ApplicationBusinessException {
		RapServidores servidorLogado = getServidorLogadoFacade().obterServidorLogado();

		MpmFichaApacheJn jn = BaseJournalFactory.getBaseJournal(operacao,
				MpmFichaApacheJn.class, servidorLogado.getUsuario());

		jn.setAtdSeq(fichaApache.getId().getAtdSeq());
		jn.setSeqp(fichaApache.getId().getSeqp());
		jn.setEgwSeq(fichaApache.getEscalaGlasgow() == null ? null
				: fichaApache.getEscalaGlasgow().getSeq());

		if (fichaApache.getServidor() != null) {
			jn.setSerMatricula(fichaApache.getServidor().getId().getMatricula());
			jn.setSerVinCodigo(fichaApache.getServidor().getId().getVinCodigo());
		}

		jn.setCriadoEm(fichaApache.getCriadoEm());
		jn.setPontTemperatura(fichaApache.getPontuacaoApacheTemperatura());
		jn.setPontPresArterialMedia(fichaApache
				.getPontuacaoApachePressaoArterialMedia());
		jn.setPontFreqCardiaca(fichaApache
				.getPontuacaoApacheFrequenciaCardiaca());
		jn.setPontFreqRespiratoria(fichaApache
				.getPontuacaoApacheFrequenciaRespiratoria());
		jn.setPontPhArterial(fichaApache.getPontuacaoApachePHArterial());
		jn.setPontSodioPlasmatico(fichaApache
				.getPontuacaoApacheSodioPlasmatico());
		jn.setPontPotassioPlasmatico(fichaApache
				.getPontuacaoApachePotassioPlasmatico());
		jn.setPontCreatininaSerica(fichaApache
				.getPontuacaoApacheCreatininaSerica());
		jn.setIndCreatSericaAguda(fichaApache.getCreatinaSericaAguda());
		jn.setPontHematocrito(fichaApache.getPontuacaoApacheHematocrito());
		jn.setPontLeucocitometria(fichaApache
				.getPontuacaoApacheLeucocitometria());
		jn.setPontIdade(fichaApache.getPontuacaoApacheIdade());
		jn.setIndClinPosOperUrg(fichaApache.getClinicoPosOperacaoUrgencia());
		jn.setIndCirurgProgramada(fichaApache.getCirurgiaProgramada());
		jn.setPontO2Fio2Maior05(fichaApache.getPontuacaoApacheO2Fio2Maior05());
		jn.setPontO2Fio2Menor05(fichaApache.getPontuacaoApacheO2Fio2Menor05());
		jn.setDthrRealizacao(fichaApache.getDthrRealizacao());
		jn.setSitEnvioAviso(fichaApache.getEnvioAviso());
		jn.setSitApache(fichaApache.getSituacaoApache());
		jn.setUnfSeq(fichaApache.getUnidadeFuncional() == null ? null
				: fichaApache.getUnidadeFuncional().getSeq());
		jn.setDthrIngressoUnidade(fichaApache.getDthrIngressoUnidade());
		jn.setIndPacCirurgico(fichaApache.getPacienteCirurgico());
		jn.setIndCirgExtracorporea(fichaApache.getCirurgiaExtracorporea());
		jn.setIndSindromeCoronariana(fichaApache.getSindromeCoronariana());
		jn.setIndChoqueCardiogenico(fichaApache.getChoqueCardiogenico());
		jn.setIndDoencaFigado(fichaApache.getDoencaFigado());
		jn.setIndDoencaCoracao(fichaApache.getDoencaCoracao());
		jn.setIndDoencaPulmao(fichaApache.getDoencaPulmao());
		jn.setIndDoencaRenal(fichaApache.getDoencaRenal());
		jn.setIndDoencaImunologica(fichaApache.getDoencaImunologica());
		jn.setIndPosOperUrg(fichaApache.getPosOperatorioUrgencia());

		return jn;
	}

	protected IPrescricaoMedicaFacade getPrescricaoMedicaFacade() {
		return prescricaoMedicaFacade;
	}

	protected IServidorLogadoFacade getServidorLogadoFacade() {
		return this.servidorLogadoFacade;
	}
		
}
