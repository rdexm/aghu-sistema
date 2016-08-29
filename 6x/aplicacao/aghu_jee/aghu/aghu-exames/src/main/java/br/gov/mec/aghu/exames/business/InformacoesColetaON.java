package br.gov.mec.aghu.exames.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;

import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioCumpriuJejumColeta;
import br.gov.mec.aghu.dominio.DominioLocalColetaAmostra;
import br.gov.mec.aghu.exames.dao.AelInformacaoColetaIDAO;
import br.gov.mec.aghu.exames.dao.AelRefCodeDAO;
import br.gov.mec.aghu.exames.dao.AelTipoAmostraExameDAO;
import br.gov.mec.aghu.model.AelRefCode;
import br.gov.mec.aghu.model.IAelInformacaoColeta;
import br.gov.mec.aghu.model.IAelInformacaoMdtoColeta;
import br.gov.mec.aghu.model.IAelItemSolicitacaoExames;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * 
 * @author cvagheti
 *
 */
@Stateless
public class InformacoesColetaON extends BaseBusiness {

	private static final long serialVersionUID = 1004122967628789411L;
	
	private static final Log LOG = LogFactory.getLog(InformacoesColetaON.class);

	@Inject
	private AelRefCodeDAO aelRefCodeDAO;
	
	@Inject
	private AelInformacaoColetaIDAO aelInformacaoColetaIDAO;
	
	@Inject
	private AelTipoAmostraExameDAO aelTipoAmostraExameDAO;
	
	@Override
	@Deprecated
	public Log getLogger() {
		return LOG;
	}

	public void processarInformacoesColeta(List<String> result, IAelInformacaoColeta info) {
		// jejum
		DominioCumpriuJejumColeta cumpriuJejum = info.getCumpriuJejum();
		String jejumRealizado = info.getJejumRealizado();

		if (DominioCumpriuJejumColeta.P.equals(cumpriuJejum)) {
			result.add("Paciente não soube informar tempo de jejum cumprido.");
		} else if (DominioCumpriuJejumColeta.N.equals(cumpriuJejum)
				&& jejumRealizado != null) {
			result.add("Paciente realizou " + jejumRealizado + " de jejum.");
		}

		// documento identidade
		Boolean apresentouDocumento = info.getDocumento();
		if (!apresentouDocumento) {
			result.add("Paciente não apresentou documento com foto para a identificação.");
		}

		// coletado por
		DominioLocalColetaAmostra localColetaAmostra = info
				.getLocalColeta();
		if (!DominioLocalColetaAmostra.C.equals(localColetaAmostra)) {
			List<AelRefCode> codes = getAelRefCodeDAO()
					.obterCodigosPorDominioELowValue("LOCAL_COLETA",
							localColetaAmostra.toString());
			result.add("Amostra coletadas por: "
					+ codes.get(0).getRvMeaning() + ".");
		}

		// informacao menstruacao
		Boolean infMenstruacao = info.getInfMenstruacao();
		Date dtUltMenstruacao = info.getDtUltMenstruacao();
		if (infMenstruacao) {
			result.add("Paciente não soube informar data da última menstruação.");
		} else if (dtUltMenstruacao != null) {
			result.add("Data da última menstruação: "
					+ DateUtil.dataToString(dtUltMenstruacao, null));
		}

		// medicacao
		String medicacao = this.medicacaoUtilizada(info);
		if (medicacao != null) {
			result.add(medicacao);
		}

		// inf adicionais
		String informacoesAdicionais = info.getInformacoesAdicionais();
		if (informacoesAdicionais != null) {
			result.add("Informações Adicionais: " + informacoesAdicionais);
		}
	}

	public List<String> criarInformacoesColeta(
			IAelItemSolicitacaoExames itemSolicitacaoExame) {
		List<String> result = new ArrayList<String>();

		// se a coleta não é feita por coletador
		if (!this.getAelTipoAmostraExameDAO().existemInformacoesColeta(
				itemSolicitacaoExame.getId().getSoeSeq(),
				itemSolicitacaoExame.getId().getSeqp(), null)) {
			return null;
		}

		List<IAelInformacaoColeta> listaInfoColetas = getAelInformacaoColetaIDAO()
				.listarInformacoesPorSoeSeq(
						itemSolicitacaoExame.getSolicitacaoExame());

		if (listaInfoColetas.isEmpty()) {
			return null;
		}

		for (IAelInformacaoColeta info : listaInfoColetas) {
			processarInformacoesColeta(result, info);
		}

		return result;
	}

	/**
	 * Retorna informações sobre medicamentos utilizados.
	 * 
	 * @param info
	 * @return
	 */
	private String medicacaoUtilizada(IAelInformacaoColeta info) {
		StringBuffer result = new StringBuffer();

		if (info.getInfMedicacao()) {
			result.append("Paciente não soube informar a medicação utilizada.");
		} else {
			Integer sizeInformacaoMdtoColetaes = info
					.getInformacaoMdtoColetaes().size();
			if (sizeInformacaoMdtoColetaes > 0) {
				result.append("Medicamentos Utilizados:\n");

				Set<? extends IAelInformacaoMdtoColeta> listaInformacaoMdtoColetaes = info
						.getInformacaoMdtoColetaes();

				for (IAelInformacaoMdtoColeta infMdto : listaInformacaoMdtoColetaes) {
					Date dthrIngeriu = infMdto.getDthrIngeriu();
					String medicamento = infMdto.getMedicamento();
					Date dthrColetou = infMdto.getDthrColetou();

					if (dthrIngeriu != null) {
						result.append(medicamento)
								.append(" Ingeriu: ")
								.append(DateUtil.dataToString(dthrIngeriu,
										"dd/MM/yyyy HH:mm"))
								.append(" Coletou: ")
								.append(DateUtil.dataToString(dthrColetou,
										"dd/MM/yyyy HH:mm"));
					} else {
						result.append(medicamento)
								.append(" Coletou: ")
								.append(DateUtil.dataToString(dthrColetou,
										"dd/MM/yyyy HH:mm"));
					}
				}

			}
		}

		return result.toString();
	}

	protected AelRefCodeDAO getAelRefCodeDAO() {
		return this.aelRefCodeDAO;
	}

	protected AelInformacaoColetaIDAO getAelInformacaoColetaIDAO() {
		return this.aelInformacaoColetaIDAO;
	}

	protected AelTipoAmostraExameDAO getAelTipoAmostraExameDAO() {
		return this.aelTipoAmostraExameDAO;
	}

}