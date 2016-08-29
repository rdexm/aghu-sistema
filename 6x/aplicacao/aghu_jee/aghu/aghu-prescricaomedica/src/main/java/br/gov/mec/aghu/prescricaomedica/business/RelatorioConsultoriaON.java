package br.gov.mec.aghu.prescricaomedica.business;

import static br.gov.mec.aghu.model.AipPacientes.VALOR_MAXIMO_PRONTUARIO;

import java.util.ArrayList;
import java.util.HashSet;
import java.util.List;
import java.util.Set;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.MpmCidAtendimento;
import br.gov.mec.aghu.model.MpmSolicitacaoConsultoria;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.prescricaomedica.dao.MpmCidAtendimentoDAO;
import br.gov.mec.aghu.prescricaomedica.dao.MpmSolicitacaoConsultoriaDAO;
import br.gov.mec.aghu.prescricaomedica.vo.BuscaConselhoProfissionalServidorVO;
import br.gov.mec.aghu.prescricaomedica.vo.PrescricaoMedicaVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioConsultoriaSubRelVO;
import br.gov.mec.aghu.prescricaomedica.vo.RelatorioConsultoriaVO;

@Stateless
public class RelatorioConsultoriaON extends BaseBusiness {


@EJB
private LaudoProcedimentoSusRN laudoProcedimentoSusRN;

private static final Log LOG = LogFactory.getLog(RelatorioConsultoriaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private MpmSolicitacaoConsultoriaDAO mpmSolicitacaoConsultoriaDAO;

@Inject
private MpmCidAtendimentoDAO mpmCidAtendimentoDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = -2967711105252802849L;

	protected LaudoProcedimentoSusRN getLaudoProcedimentoSusRn() {
		return laudoProcedimentoSusRN;
	}

	protected MpmSolicitacaoConsultoriaDAO getMpmSolicitacaoConsultoriaDAO() {
		return mpmSolicitacaoConsultoriaDAO;
	}

	/**
	 * Retorna a VO com os dados que serão colocados no relatório
	 * 
	 * @param presc
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<RelatorioConsultoriaVO> gerarRelatorioConsultoria(
			RapServidores servidor, AghAtendimentos atd,
			PrescricaoMedicaVO prescricao, Boolean validarIndImpSolicConsultoria) throws ApplicationBusinessException {

		if ((servidor == null) || (atd == null) || (prescricao == null)) {
			throw new IllegalArgumentException("Parâmetro obrigatório");
		}

		if (validarIndImpSolicConsultoria == null) {
			validarIndImpSolicConsultoria = true;
		}
		// pega a consultoria solicitada para este atendimento
		// MpmPrescricaoMedicaId idPrescricao = new
		// MpmPrescricaoMedicaId(atdSeq,
		// seq);
		List<MpmSolicitacaoConsultoria> listaConsultoria = this
				.getMpmSolicitacaoConsultoriaDAO().buscaSolicitacaoConsultoria(prescricao.getId(), true);
		List<RelatorioConsultoriaVO> listaRelatorioVO = new ArrayList<RelatorioConsultoriaVO>(0);
		
		for (int i = 0; i < listaConsultoria.size(); ++i) {
			this.processarSolicConsultoria(servidor, atd, prescricao, validarIndImpSolicConsultoria, listaRelatorioVO, listaConsultoria, i);
		}

		return listaRelatorioVO;
	}

	private void processarSolicConsultoria(RapServidores servidor, AghAtendimentos atd,
			PrescricaoMedicaVO prescricao, Boolean validarIndImpSolicConsultoria, 
			List<RelatorioConsultoriaVO> listaRelatorioVO,
			List<MpmSolicitacaoConsultoria> listaConsultoria, int i) throws ApplicationBusinessException {
		// só vai gerar relatório para aquelas consultorias que tem o
		// indicador "IND_IMP_SOLIC_CONSULTORIA" com valor "S" e se a
		// prescricao
		// está vigente.
		// o indicar avaliado pertence a especialidade da consultoria e não
		// do atendimento do paciente.

		boolean vigente = false;
		
		if ((listaConsultoria.get(i).getDthrSolicitada().compareTo(prescricao.getDthrInicio()) >= 0)
				&& ((prescricao.getDthrFim() == null) || 
						(prescricao.getDthrFim() != null && listaConsultoria.get(i).getDthrSolicitada()
						.before(prescricao.getDthrFim())))) {
			vigente = true;
		}
		if (getIndImpSolicConsultoria(validarIndImpSolicConsultoria, listaConsultoria, i) && vigente) {

			RelatorioConsultoriaVO relatorio = new RelatorioConsultoriaVO();

			BuscaConselhoProfissionalServidorVO conselho = new BuscaConselhoProfissionalServidorVO();
			conselho = this.getLaudoProcedimentoSusRn()
					.buscaConselhoProfissionalServidorVO(
							listaConsultoria.get(i).getServidorCriacao()
									.getId().getMatricula(),
							listaConsultoria.get(i).getServidorCriacao()
									.getId().getVinCodigo());

			if (conselho != null) {
				relatorio.setNomeMedico(conselho.getNome());
				relatorio.setConselho(conselho.getSiglaConselho());
				relatorio.setCodConselho(conselho
						.getNumeroRegistroConselho());
			} else {
				relatorio.setNomeMedico(listaConsultoria.get(i)
						.getServidorCriacao().getPessoaFisica().getNome());
			}

			relatorio.setNomePaciente(atd.getPaciente().getNome());

			// paciente recém nacido requer prontuário da mãe no
			// relatório
			if (atd.getPaciente().getProntuario() != null
					&& atd.getPaciente().getProntuario() > VALOR_MAXIMO_PRONTUARIO) {

				if (atd.getAtendimentoMae() != null) {
					relatorio.setProntuario(CoreUtil
							.formataProntuarioRelatorio(atd.getPaciente()
									.getProntuario())
							+ "          "
							+ "Mãe"
							+ ": "
							+ CoreUtil.formataProntuarioRelatorio(atd
									.getAtendimentoMae().getPaciente()
									.getProntuario()));
				} else {
					relatorio.setProntuario(CoreUtil
							.formataProntuarioRelatorio(atd.getPaciente()
									.getProntuario()));
				}

			} else {
				relatorio.setProntuario(CoreUtil
						.formataProntuarioRelatorio(atd.getPaciente()
								.getProntuario()));
			}

			if (atd.getLeito() != null) {
				relatorio.setLeito("Leito: " + atd.getLeito().getLeitoID());
			} else if (atd.getQuarto() != null) {
				relatorio
						.setLeito("Quarto: " + atd.getQuarto().getDescricao());
			} else {

				StringBuffer unidade = new StringBuffer("Unidade: ");

				if (atd.getUnidadeFuncional().getSigla() != null) {
					unidade.append(atd.getUnidadeFuncional().getSigla()).append(' ');
				}
				unidade.append(atd.getUnidadeFuncional().getAndar())
						.append(' ')
						.append(atd.getUnidadeFuncional().getIndAla()
								.getDescricao());
				relatorio.setLeito(unidade.toString());
			}

			if (listaConsultoria.size() > 0) {
				relatorio.setSolicitadoEm(listaConsultoria.get(i)
						.getDthrSolicitada());
			}

			relatorio.setTipoConsultoria(listaConsultoria.get(i).getTipo()
					.getDescricao().toUpperCase());

			// popula os dados do subrelatório.
			List<RelatorioConsultoriaSubRelVO> subRel = montagemSubRelatorio(
					listaConsultoria.get(i), atd);
			relatorio.setDadosSubRel(subRel);

			listaRelatorioVO.add(relatorio);
		}

	}
	
	// Correção #44802. Isolando regra de verificação de índice de solicitação de consultoria somente para a estória #1107 e não mais para a #5258, cfe. AGHWEB
	private boolean getIndImpSolicConsultoria(Boolean validarIndImpSolicConsultoria, List<MpmSolicitacaoConsultoria> listaConsultoria, int i) {
		boolean indImpSolicConsultoria = true;
		if (validarIndImpSolicConsultoria) {
			indImpSolicConsultoria = listaConsultoria.get(i).getEspecialidade().getIndImpSolicConsultoria();
		}
		return indImpSolicConsultoria;
	}

	/**
	 * Monta os dados do subrelatório de consultoria.
	 * 
	 * @param consultoria
	 * @param atd
	 * @return
	 */
	public List<RelatorioConsultoriaSubRelVO> montagemSubRelatorio(
			MpmSolicitacaoConsultoria consultoria, AghAtendimentos atd) {

		// pega a lista de cidAtendimentos para este atendimento e concatena as
		// descrições no campos de
		// diagnóstico da VO
		//Set<MpmCidAtendimento> listaCidAtd = atd.getCidAtendimentos();
		Set<MpmCidAtendimento> listaCidAtd = new HashSet<MpmCidAtendimento>(this.mpmCidAtendimentoDAO.listar(atd));
		StringBuffer diagnostico = new StringBuffer();

		for (MpmCidAtendimento mca : listaCidAtd) {
			if (mca.getDthrFim() == null) {
				diagnostico.append(mca.getCid().getCodigoDescricaoCompletaCID()).append("; ");
			}
		}

		RelatorioConsultoriaSubRelVO sub = new RelatorioConsultoriaSubRelVO();
		if (consultoria != null) {

			String espec = consultoria.getEspecialidade().getSigla() + " - "
					+ consultoria.getEspecialidade().getNomeEspecialidade();

			sub.setEspecialidade(espec);

			if (consultoria.getMotivo() != null) {
				sub.setMotivo(consultoria.getMotivo());
			}
		}
		sub.setDiagnostico(diagnostico.toString());

		List<RelatorioConsultoriaSubRelVO> lista = new ArrayList<RelatorioConsultoriaSubRelVO>();
		lista.add(sub);

		return lista;
	}
	
}
