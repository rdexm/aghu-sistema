package br.gov.mec.aghu.exames.business;

import java.util.Calendar;
import java.util.Date;
import java.util.GregorianCalendar;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.pesquisa.vo.RelatorioFichaTrabalhoPatologiaClinicaVO;
import br.gov.mec.aghu.faturamento.business.IFaturamentoFacade;
import br.gov.mec.aghu.model.AelSolicitacaoExames;
import br.gov.mec.aghu.model.AipPacientes;
import br.gov.mec.aghu.model.FatConvenioSaude;
import br.gov.mec.aghu.model.FatConvenioSaudePlano;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;

@Stateless
public class RelatorioFichaTrabalhoPatologiaClinicaON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioFichaTrabalhoPatologiaClinicaON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@EJB
private IFaturamentoFacade faturamentoFacade;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private IPacienteFacade pacienteFacade;

	private static final long serialVersionUID = 4062266481609587577L;

	public List<RelatorioFichaTrabalhoPatologiaClinicaVO> obterFichaTrabPorExame(Integer amoSoeSeq, Short amoSoeSeqP, Boolean recebeAmostra, Short unfSeq) {
		List<RelatorioFichaTrabalhoPatologiaClinicaVO> vos = getAelItemSolicitacaoExameDAO().obterListaExamesPatologiaClinica(amoSoeSeq, amoSoeSeqP, recebeAmostra, unfSeq);
		for(RelatorioFichaTrabalhoPatologiaClinicaVO vo : vos) {
			if(vo != null) {
				if(vo.getMatriculaResponsavel() != null && vo.getVinCodigoResponsavel() != null) {
					RapServidores resp = getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(new RapServidoresId(vo.getMatriculaResponsavel(), vo.getVinCodigoResponsavel()));
					vo.setNomeResponsavel(resp.getPessoaFisica().getNome());
				}
				//Localização
				setRelLocalizacao(vo);
				
				//DADOS PACIENTE
				AelSolicitacaoExames sol = getAelSolicitacaoExameDAO().obterPorChavePrimaria(amoSoeSeq);
				//CODIGO PACIENTE
				if(sol.getAtendimento() != null) {
					vo.setPacCodigo(sol.getAtendimento().getPaciente().getCodigo());
					final AipPacientes paciente = getPacienteFacade().obterAipPacientesPorChavePrimaria(vo.getPacCodigo());
					vo.setNomePaciente(paciente.getNome());
					vo.setSexo(paciente.getSexo());
					vo.setProntuario(paciente.getProntuario());
					vo.setIdade(paciente.getIdadeAnoMesFormat());
					vo.setDtNascimento(paciente.getDtNascimento());
					
					//ORIGEM
					if(DominioOrigemAtendimento.N.equals(sol.getAtendimento().getOrigem())) {
						vo.setOrigem(DominioOrigemAtendimento.I.getDescricao());
					}
					else {
						vo.setOrigem(sol.getAtendimento().getOrigem().getDescricao());
					}
				}
				else {
					vo.setPacCodigo(sol.getAtendimentoDiverso().getAipPaciente() != null ? sol.getAtendimentoDiverso().getAipPaciente().getCodigo() : null);
					
					if (vo.getPacCodigo() == null) {
						vo.setNomePaciente(sol.getAtendimentoDiverso().getNomePaciente());
						vo.setSexo(sol.getAtendimentoDiverso().getSexo());
						vo.setProntuario(sol.getAtendimentoDiverso().getProntuario());
						vo.setIdade(this.formatarIdadeAnoMes(sol.getAtendimentoDiverso().getDtNascimento()));
						vo.setDtNascimento(sol.getAtendimentoDiverso().getDtNascimento());
					}
					//ORIGEM
					if(sol.getAtendimentoDiverso() != null) {
						if(sol.getAtendimentoDiverso().getAbsCandidatosDoadores() != null &&  
								sol.getAtendimentoDiverso().getAbsCandidatosDoadores().getSeq() != null) {
							vo.setOrigem(DominioOrigemAtendimento.D.getDescricao());
						}
					}
					else {
						vo.setOrigem(null);
					}
				}
				//NOME PACIENTE
				
				//CONVÊNIO - PLANO
				String convSaude = "";
				String convSaudePlan = "";
				if(vo.getCspSeq()!= null) {
					FatConvenioSaude convSau = getFaturamentoFacade().obterFatConvenioSaudePorId(vo.getCspCnvCodigo());
					convSaude = convSau.getDescricao();
				}
				if(vo.getCspCnvCodigo() != null && vo.getCspSeq() != null) {
					FatConvenioSaudePlano convSauPlan = getFaturamentoFacade().obterFatConvenioSaudePlano(vo.getCspCnvCodigo(), vo.getCspSeq());
					convSaudePlan = convSauPlan.getDescricao();
				}
				vo.setConvenioPlano(convSaude + " / " + convSaudePlan);
			}
		}
		
		return vos;
	}

	private void setRelLocalizacao(RelatorioFichaTrabalhoPatologiaClinicaVO vo) {
		if(vo.getLeitoID() != null){
			vo.setLocalizacao(vo.getLeitoID());
		}
	}
	
	private String formatarIdadeAnoMes(final Date dt) {
		String tempo = "anos";
		String tempoMes = "meses";
		Integer idadeMes = null;
		String idadeFormat = null;
		if (dt != null) {
			Calendar dataNascimento = new GregorianCalendar();
			dataNascimento.setTime(dt);
			Calendar dataCalendario = new GregorianCalendar();
			dataCalendario.setTime(new Date());
			// ObtÃ©m a idade baseado no ano
			Integer idadeNum = dataCalendario.get(Calendar.YEAR) - dataNascimento.get(Calendar.YEAR);
			// dataNascimento.add(Calendar.YEAR, idadeNum);

			if (dataCalendario.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
				idadeNum--;
			} else if (dataCalendario.get(Calendar.MONTH) == dataNascimento.get(Calendar.MONTH)
					&& dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
				idadeNum--;
			}

			if (idadeNum == 1) {
				tempo = "ano";
			}

			if (dataCalendario.get(Calendar.MONTH) < dataNascimento.get(Calendar.MONTH)) {
				idadeMes = dataCalendario.get(Calendar.MONTH) + (12 - dataNascimento.get(Calendar.MONTH));
			} else {
				idadeMes = dataCalendario.get(Calendar.MONTH) - dataNascimento.get(Calendar.MONTH);
				if (dataCalendario.get(Calendar.DAY_OF_MONTH) < dataNascimento.get(Calendar.DAY_OF_MONTH)) {
					idadeMes--;
				}
			}

			if (idadeMes < 0) {
				if (12 + (idadeMes) == 1) {
					tempoMes = 12 + (idadeMes) + " mês";
				} else {
					tempoMes = 12 + (idadeMes) + " meses";
				}
			} else {
				if (idadeMes == 1) {
					tempoMes = idadeMes + " mês";
				} else {
					tempoMes = idadeMes + " meses";
				}
			}

			idadeFormat = idadeNum + " " + tempo + " " + tempoMes;
		}

		return idadeFormat;
	}

	protected IPacienteFacade getPacienteFacade() {
		return pacienteFacade;
	}

	protected IFaturamentoFacade getFaturamentoFacade() {
		return faturamentoFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

	protected AelItemSolicitacaoExameDAO getAelItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}
	
}
