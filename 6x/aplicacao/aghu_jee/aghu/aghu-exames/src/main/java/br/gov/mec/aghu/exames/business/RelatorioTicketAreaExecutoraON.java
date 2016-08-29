package br.gov.mec.aghu.exames.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.dominio.DominioOrigemAtendimento;
import br.gov.mec.aghu.dominio.DominioOrigemSolicitacao;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.dominio.DominioTipoColeta;
import br.gov.mec.aghu.dominio.DominioTipoTransporteUnidade;
import br.gov.mec.aghu.exames.dao.AelAtendimentoDiversosDAO;
import br.gov.mec.aghu.exames.dao.AelItemSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.dao.AelSolicitacaoExameDAO;
import br.gov.mec.aghu.exames.solicitacao.business.ISolicitacaoExameFacade;
import br.gov.mec.aghu.exames.vo.RelatorioTicketAreaExecutoraVO;
import br.gov.mec.aghu.model.AelAtendimentoDiversos;
import br.gov.mec.aghu.model.AelItemSolicitacaoExames;
import br.gov.mec.aghu.model.AghAtendimentos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.registrocolaborador.business.IRegistroColaboradorFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.BaseException;
import br.gov.mec.aghu.core.utils.DateUtil;

@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class RelatorioTicketAreaExecutoraON extends BaseBusiness {

private static final Log LOG = LogFactory.getLog(RelatorioTicketAreaExecutoraON.class);

@Override
@Deprecated
protected Log getLogger() {
return LOG;
}


@Inject
private AelItemSolicitacaoExameDAO aelItemSolicitacaoExameDAO;

@Inject
private AelSolicitacaoExameDAO aelSolicitacaoExameDAO;

@Inject
private AelAtendimentoDiversosDAO aelAtendimentoDiversosDAO;

@EJB
private IRegistroColaboradorFacade registroColaboradorFacade;

@EJB
private ISolicitacaoExameFacade solicitacaoExameFacade;

@EJB
private IAghuFacade aghuFacade;
	
	/**
	 * 
	 */
	private static final long serialVersionUID = -1510557325911490774L;

	@SuppressWarnings("PMD.ExcessiveMethodLength")
	public List<RelatorioTicketAreaExecutoraVO> pesquisarRelatorioTicketAreaExecutora(Integer soeSeq, Short unfSeq, String nomeMicrocomputador)  throws BaseException {
		
		List<RelatorioTicketAreaExecutoraVO> listaRetorno = new ArrayList<RelatorioTicketAreaExecutoraVO>();
		List<Object[]> resultadoPesquisa = getAelSolicitacaoExameDAO().pesquisarRelatorioTicketAreaExecutora(soeSeq, unfSeq);
		if (resultadoPesquisa.isEmpty()) {
			return listaRetorno;
		}
		
		RelatorioTicketAreaExecutoraVO relatorioTicketAreaExecutoraVO = null;
		SimpleDateFormat sdf1 = new SimpleDateFormat("dd/MM/yyyy HH:mm");
		SimpleDateFormat sdf2 = new SimpleDateFormat("dd/MM/yyyy");

		Iterator<Object[]> it = resultadoPesquisa.iterator();

		while (it.hasNext()) {

			Object[] obj = it.next();
			relatorioTicketAreaExecutoraVO = new RelatorioTicketAreaExecutoraVO();

			// Numero foi fornecido por VAelSolicAtends
			if (obj[0] != null) {
				relatorioTicketAreaExecutoraVO.setSoeSeq((Integer) obj[0]);

			}

			if (obj[1] != null) {
				relatorioTicketAreaExecutoraVO.setInfClinicas((String) obj[1]);

			}

			if (obj[2] != null) {
				Date criadoEm = (Date) obj[2];
				relatorioTicketAreaExecutoraVO.setCriadoEm(sdf1.format(criadoEm));

			}
			
			if (obj[3] != null) {
				String descMaterialAnalise = (String) obj[3];
				relatorioTicketAreaExecutoraVO.setDescricaoMaterialAnalise(descMaterialAnalise);

			}

			AghAtendimentos atendimento = null;
			if ((Integer) obj[19] != null) {
				atendimento = 
					//getAghAtendimentosDAO().obterPorChavePrimaria((Integer) obj[19]);
					this.getAghuFacade().obterAghAtendimentoPorChavePrimaria((Integer) obj[19]);
			}

			if (obj[4] != null) {
				/*AelAtendimentoDiversos atendimentoDiversos = null;
				if ((Integer) obj[18] != null) {
					atendimentoDiversos = getAtendimentosDiversosDAO()
							.obterPorChavePrimaria((Integer) obj[18]);
				}*/
				String cvn = (String) obj[4];
				String csp = (String) obj[5];
				/*relatorioTicketAreaExecutoraVO.setDescConvenio(cvn
						+ buscarProjetoAtend(atendimento,
								atendimentoDiversos));*/
				relatorioTicketAreaExecutoraVO.setDescConvenio(cvn+" - "+csp);
			}

			if ((String) obj[6] != null) {
				relatorioTicketAreaExecutoraVO.setLeito("Leito: " + (String) obj[6]);

			} else if (atendimento != null
					&& atendimento.getQuarto() != null
					&& atendimento.getQuarto().getNumero() != null) {
				relatorioTicketAreaExecutoraVO.setLeito("Quarto: "
						+ atendimento.getQuarto().getNumero()
								.toString());

			} else if (atendimento != null
					&& atendimento.getUnidadeFuncional() != null
					&& atendimento.getUnidadeFuncional().getSigla() != null) {
				relatorioTicketAreaExecutoraVO.setLeito("Unidade: "
						+ atendimento.getUnidadeFuncional().getSigla());
			}

			if (obj[7] != null) {
				relatorioTicketAreaExecutoraVO.setProntuario(CoreUtil.formataProntuarioRelatorio(obj[7]));

			}

			if (obj[8] != null) {
				relatorioTicketAreaExecutoraVO.setNome((String) obj[8]);
			}

			if (obj[9] != null) {
				relatorioTicketAreaExecutoraVO.setSeqp((Short) obj[9]);
				// //Consulta Horário Agendado
				// Object[] result = (Object[])
				// executeCriteriaUniqueResult(obterCriteriaHorarioAgendado(soeSeq,
				// (Short)obj[7] ));
				// if(result!= null && result[3] !=null)
				// vo.setDthrProgramada(sdf1.format((Date)result[3]));

			}

			if (obj[10] != null) {
				DominioTipoColeta tipoColeta = (DominioTipoColeta) obj[10];
				relatorioTicketAreaExecutoraVO.setTipoColeta(tipoColeta.getDescricao());
			}

			if (obj[11] != null) {
				relatorioTicketAreaExecutoraVO.setDthrProgramada(sdf1.format((Date) obj[11]));
			}

			if (obj[12] != null) {
				relatorioTicketAreaExecutoraVO.setIndUso02((Boolean) obj[12]);
			}

			if (obj[13] != null) {
				DominioTipoTransporteUnidade tipoTrasnporte = (DominioTipoTransporteUnidade) obj[13];
				relatorioTicketAreaExecutoraVO.setTipoTransporte(tipoTrasnporte.getDescricao());
			}

			if (obj[14] != null) {
				relatorioTicketAreaExecutoraVO.setDescExame((String) obj[14]+" / "+(String) obj[27]);
			}

			if (obj[15] != null) {
				// --DECODE(VAS.IND_OBJETIVO_SOLIC,'1','1º
				// Exame','2','Exame Comparativo') IND_OBJETIVO_SOLIC
				DominioOrigemSolicitacao origem = (DominioOrigemSolicitacao) obj[15];
				if (origem.equals(DominioOrigemSolicitacao.COMPARATIVO)) {
					relatorioTicketAreaExecutoraVO.setIndObjSolic("Exame " + origem.getDescricao());
				} else {
					relatorioTicketAreaExecutoraVO.setIndObjSolic(origem.getDescricao());
				}
			}

			if (obj[16] != null) {
				relatorioTicketAreaExecutoraVO.setCodigoPac((Integer) obj[16]);
			}

			if (obj[17] != null) {
				DominioSexo sexo = (DominioSexo) obj[17];
				relatorioTicketAreaExecutoraVO.setSexo(sexo.toString());
			}

			if (obj[18] != null) {
				relatorioTicketAreaExecutoraVO.setDtNascimento((Date) obj[18]);
				relatorioTicketAreaExecutoraVO.setDataNascimento(sdf2.format((Date) obj[18]));
			}
			if (atendimento != null) {
				if(atendimento.getOrigem().equals(DominioOrigemAtendimento.N)){
					relatorioTicketAreaExecutoraVO.setOrigem(DominioOrigemAtendimento.I.getDescricao());	
				} else {
					relatorioTicketAreaExecutoraVO.setOrigem(atendimento.getOrigem().getDescricao());
				} 
			} else if (obj[20] != null) {
				AelAtendimentoDiversos atendimentoDiverso = null;
				if ((Integer) obj[20] != null) {
					atendimentoDiverso = getAtendimentosDiversosDAO()
							.obterPorChavePrimaria((Integer) obj[20]);
					if(atendimentoDiverso!=null){
						relatorioTicketAreaExecutoraVO.setOrigem(DominioOrigemAtendimento.D.getDescricao());		
					}
				}
			} else {
				relatorioTicketAreaExecutoraVO.setOrigem(null);
			}

			if (obj[21] != null) {
				relatorioTicketAreaExecutoraVO.setSerVinCodigo((Short) obj[21]);
			}
			if (obj[22] != null) {
				relatorioTicketAreaExecutoraVO.setSerMatricula((Integer) obj[22]);
			}
			
			if (obj[23] != null && obj[24] != null) {
				Short serVinCodigoResponsavel = (Short) obj[23];
				Integer serMatricula = (Integer)obj[24];
				RapServidoresId servidorId = new RapServidoresId();
				servidorId.setMatricula(serMatricula);
				servidorId.setVinCodigo(serVinCodigoResponsavel);
				RapServidores servidor = this.getRegistroColaboradorFacade().obterRapServidoresPorChavePrimaria(servidorId);
				
				relatorioTicketAreaExecutoraVO.setNomeServidorResponsavel(servidor.getPessoaFisica().getNome());
			}
			if (obj[25] != null) {
				Short unf2Seq = (Short) obj[25];
				AghUnidadesFuncionais unidadesFuncional = 
					//this.getAghUnidadesFuncionaisDAO().obterPorChavePrimaria(unf2Seq);
					this.getAghuFacade().obterAghUnidadesFuncionaisPorChavePrimaria(unf2Seq);
				if(unidadesFuncional.getSigla()!=null){
					relatorioTicketAreaExecutoraVO.setDescUnidadeFuncional(unidadesFuncional.getSeq().toString()+" "+unidadesFuncional.getSigla()+" - "+unidadesFuncional.getDescricao());	
				} else {
					relatorioTicketAreaExecutoraVO.setDescUnidadeFuncional(unidadesFuncional.getSeq().toString()+" - "+unidadesFuncional.getDescricao());	
				}
				relatorioTicketAreaExecutoraVO.setAndar(unidadesFuncional.getAndar());
				if(unidadesFuncional.getIndAla() != null) {
					relatorioTicketAreaExecutoraVO.setAla(unidadesFuncional.getIndAla().getCodigo());
				}
			}
			if (obj[26] != null) {
				String descRegiaoAnatomica = (String) obj[26];
				relatorioTicketAreaExecutoraVO.setDescRegiaoAnatomica(descRegiaoAnatomica);
			}
			if (obj[27] != null) {
				String descricaoMaterial = (String) obj[27];
				relatorioTicketAreaExecutoraVO.setDescricaoMaterial(descricaoMaterial);
			}
			if(relatorioTicketAreaExecutoraVO.getDataNascimento()!=null){
				String idade = DateUtil.obterIdadeFormatadaAnoMesDia(relatorioTicketAreaExecutoraVO.getDtNascimento());
				relatorioTicketAreaExecutoraVO.setIdade(idade);
			}
		listaRetorno.add(relatorioTicketAreaExecutoraVO);
		}
		
		if (relatorioTicketAreaExecutoraVO != null) {
			
			//Atualiza ItemSolicitacaoExame após impressão do relatório
			AelItemSolicitacaoExames itemSolicitacaoExames = getItemSolicitacaoExameDAO().obterPorId(relatorioTicketAreaExecutoraVO.getSoeSeq(), relatorioTicketAreaExecutoraVO.getSeqp());
			if(itemSolicitacaoExames != null){
				itemSolicitacaoExames.setIndImprimiuTicket(true);
				getSolicitacaoExameFacade().atualizar(itemSolicitacaoExames, nomeMicrocomputador);
				getItemSolicitacaoExameDAO().flush();
			}
		}
		
		return listaRetorno;		
	}
	
	/*private String buscarProjetoAtend(AghAtendimentos atendimento,
			AelAtendimentoDiversos atendimentoDiversos) {
		AelProjetoPesquisas projeto = new AelProjetoPesquisas();

		if (atendimento != null) {

			if (atendimento.getOrigem().equals(DominioOrigemAtendimento.I)) {
				AinInternacao internacao = atendimento.getInternacao();
				projeto = internacao.getProjetoPesquisa();

			} else if (atendimento.getOrigem().equals(
					DominioOrigemAtendimento.A)
					&& atendimento.getConsulta() != null) {

				AacConsultas consulta = atendimento.getConsulta();
				projeto = consulta.getProjetoPesquisa();

			} else if (atendimento.getOrigem().equals(
					DominioOrigemAtendimento.C)) {
				List<MbcCirurgias> cirurgias = getMbcCirurgiasDAO()
						.pesquisarCirurgiaPorAtendimento(atendimento.getSeq());
				MbcCirurgias cirurgia = null;

				if (cirurgias != null && !cirurgias.isEmpty()) {
					cirurgia = cirurgias.get(cirurgias.size() - 1);
					projeto = cirurgia.getProjetoPesquisa();
				}
			}
		} else if (atendimentoDiversos != null) {
			projeto = atendimentoDiversos.getAelProjetoPesquisas();
		}

		String projetoDesc = "";
		if (projeto != null && projeto.getNumero() != null
				&& projeto.getNome() != null) {
			projetoDesc = projeto.getNumero() + " - " + projeto.getNome();
		}

		return projetoDesc;
	}*/
	
	protected AelAtendimentoDiversosDAO getAtendimentosDiversosDAO() {
		return aelAtendimentoDiversosDAO;
	}
	
	protected AelSolicitacaoExameDAO getAelSolicitacaoExameDAO() {
		return aelSolicitacaoExameDAO;
	}	

	protected AelItemSolicitacaoExameDAO getItemSolicitacaoExameDAO() {
		return aelItemSolicitacaoExameDAO;
	}
	
	protected ISolicitacaoExameFacade getSolicitacaoExameFacade() {
		return this.solicitacaoExameFacade;
	}
	
	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

	protected IRegistroColaboradorFacade getRegistroColaboradorFacade() {
		return this.registroColaboradorFacade;
	}

}
