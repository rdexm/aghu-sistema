package br.gov.mec.aghu.controleinfeccao.business;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.blococirurgico.business.IBlocoCirurgicoFacade;
import br.gov.mec.aghu.controleinfeccao.vo.AtendimentoPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.FiltroListaPacienteCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.NotificacoesGeraisVO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioBuscaAtivaPacientesCCIHVO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioBuscaAtivaPacientesVO;
import br.gov.mec.aghu.controleinfeccao.vo.RelatorioBuscaAtivaUnidadePacientesVO;
import br.gov.mec.aghu.model.MpmItemPrescricaoMdto;
import br.gov.mec.aghu.paciente.business.IPacienteFacade;
import br.gov.mec.aghu.prescricaomedica.business.IPrescricaoMedicaFacade;
import br.gov.mec.aghu.prescricaomedica.vo.ItemMedicamentoPrescricaoMedicaDetalheVO;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.core.utils.StringUtil;

@Stateless
public class RelatorioBuscaAtivaPacientesON extends BaseBusiness {

	private static final long serialVersionUID = -7642794213040981489L;

	private static final Log LOG = LogFactory.getLog(RelatorioBuscaAtivaPacientesON.class);
	
	@Override
	@Deprecated	
	protected Log getLogger() {
		return LOG;
	}
	
	@EJB
	private IPacienteFacade pacienteFacade;
	
	@EJB
	private IControleInfeccaoFacade controleInfeccaoFacade;
	
	@EJB
	private IBlocoCirurgicoFacade blocoCirurgicoFacade;
	
	@EJB
	private IPrescricaoMedicaFacade prescricaoMedicaFacade;
	
	private static final String FORMATO_DATA = "dd/MM/yyyy";
	private static final String FORMATO_DATA_HORA = "dd/MM/yyyy HH:mm";

	public List<RelatorioBuscaAtivaPacientesCCIHVO> gerarRelatorioBuscaAtivaPacientes(FiltroListaPacienteCCIHVO filtro) {
		Short unfSeq;
		RelatorioBuscaAtivaPacientesCCIHVO vo = new RelatorioBuscaAtivaPacientesCCIHVO();
		List<RelatorioBuscaAtivaUnidadePacientesVO> unidades = new ArrayList<RelatorioBuscaAtivaUnidadePacientesVO>();
		RelatorioBuscaAtivaUnidadePacientesVO unidadeCorrente;
		List<RelatorioBuscaAtivaPacientesVO> pacientesPorUnidade = new ArrayList<RelatorioBuscaAtivaPacientesVO>();
		List<AtendimentoPacientesCCIHVO> listaPacientes = this.pacienteFacade.pesquisarPacientesBuscaAtivaCCIH(filtro);

		if (!listaPacientes.isEmpty()) {
			unfSeq = listaPacientes.get(0).getUnfSeq();
			unidadeCorrente = montarUnidades(listaPacientes.get(0));
			unidadeCorrente.setListaPacientes(new ArrayList<RelatorioBuscaAtivaPacientesVO>());
			
			for (AtendimentoPacientesCCIHVO paciente : listaPacientes) {
				if ((unfSeq == null) || unfSeq.equals(paciente.getUnfSeq())) {
					pacientesPorUnidade.add(montarDadosPaciente(paciente));
				} else {
					unidadeCorrente.setListaPacientes(pacientesPorUnidade);
					unidades.add(unidadeCorrente);
					unfSeq = paciente.getUnfSeq();
					unidadeCorrente = montarUnidades(paciente);
					unidadeCorrente.setListaPacientes(new ArrayList<RelatorioBuscaAtivaPacientesVO>());
					pacientesPorUnidade = new ArrayList<RelatorioBuscaAtivaPacientesVO>();
					pacientesPorUnidade.add(montarDadosPaciente(paciente));
				}
			}
			
			unidadeCorrente.setListaPacientes(pacientesPorUnidade);
			unidades.add(unidadeCorrente);
		}
		
		vo.setUnidades(unidades);
		List<RelatorioBuscaAtivaPacientesCCIHVO> lista = new ArrayList<RelatorioBuscaAtivaPacientesCCIHVO>();
		vo.setDataGeracao(DateUtil.obterDataFormatada(new Date(), FORMATO_DATA_HORA));
		lista.add(vo);
		
		return lista;
	}	
	
	public RelatorioBuscaAtivaUnidadePacientesVO montarUnidades(AtendimentoPacientesCCIHVO pacienteUnidade) {
		RelatorioBuscaAtivaUnidadePacientesVO unidade = new RelatorioBuscaAtivaUnidadePacientesVO();
		StringBuilder builder = new StringBuilder("");
		
		if (pacienteUnidade.getUnfSeq() != null) {
			builder.append(pacienteUnidade.getUnfSeq().toString());
		} 
		
		if (pacienteUnidade.getUnfDescricao() != null) {
			builder.append(" - ").append(pacienteUnidade.getUnfDescricao());
		}
		
		unidade.setUnidadeDescricao(builder.toString());
		
		return unidade;
	}
	
	public RelatorioBuscaAtivaPacientesVO montarDadosPaciente(AtendimentoPacientesCCIHVO paciente) {
		RelatorioBuscaAtivaPacientesVO vo = new RelatorioBuscaAtivaPacientesVO();

		if (paciente.getProntuario() != null) {
			vo.setProntuario(CoreUtil.formataProntuario(paciente.getProntuario()));
		}

		vo.setNome(paciente.getNome());
		
		if (paciente.getDtNascimento() != null) {
			vo.setIdade(blocoCirurgicoFacade.obterIdadePorDataNascimento(paciente.getDtNascimento()));
		}

		if (paciente.getLeitoId() != null) {
			vo.setLeitoId(paciente.getLeitoId());
		} else {
			vo.setLeitoId("");
		}

		if (paciente.getDataHoraIngresso() != null) {
			vo.setDataHoraIngresso(DateUtil.obterDataFormatadaHoraMinutoSegundo(paciente.getDataHoraIngresso()));
		} else {
			vo.setDataHoraIngresso("");
		}
		
		if (paciente.getDtInicioInternacao() != null) {
			vo.setDataHoraInicio(DateUtil.obterDataFormatada(paciente.getDtInicioInternacao(), FORMATO_DATA));
		} else {
			vo.setDataHoraInicio("");
		}

		// popula detalhes
		List<NotificacoesGeraisVO> doencas = this.controleInfeccaoFacade.listarNotificacoesDoencasCondicoesBuscaAtiva(paciente.getCodPaciente(), paciente.getAtdSeq()); // C3
		List<NotificacoesGeraisVO> topografias = this.controleInfeccaoFacade.listarNotificacoesTopografiasBuscaAtiva(paciente.getCodPaciente(), paciente.getAtdSeq()); // C4
		List<NotificacoesGeraisVO> fatoresPredisponentes = this.controleInfeccaoFacade.listarNotificacoesFatoresPredisponentesBuscaAtiva(paciente.getCodPaciente(), paciente.getAtdSeq()); // C6
		List<NotificacoesGeraisVO> procedimentos = this.controleInfeccaoFacade.listarNotificacoesProcedimentosRiscoBuscaAtiva(paciente.getCodPaciente(),paciente.getAtdSeq()); // C5
		List<NotificacoesGeraisVO> cirurgias = this.blocoCirurgicoFacade.listarNotificacoesCirurgiaParaBuscaAtiva(paciente.getCodPaciente(), paciente.getAtdSeq()); // C8
		List<MpmItemPrescricaoMdto> medicamentos = this.prescricaoMedicaFacade.listarPrescMedicamentoDetalhePorPacienteAtendimento(paciente.getCodPaciente(), paciente.getAtdSeq()); // C9
		List<NotificacoesGeraisVO> notasCCIH = this.controleInfeccaoFacade.listarNotasCCIHNaoEncerradas(paciente.getCodPaciente()); // C7

		vo.setNotificacaoDoencas(formatarCampos(doencas));
		vo.setNotificacaoTopografias(formatarCampos(topografias));
		vo.setNotificacaoFatPredisponentes(formatarCampos(fatoresPredisponentes));
		vo.setNotificacaoProcedimentos(formatarCampos(procedimentos));
		vo.setNotificacaoCirurgias(formatarCampos(cirurgias));
		vo.setDetalhesMedicamentos(montarMedicamentosVO(medicamentos));
		vo.setNotificacaoNotas(formatarCampos(notasCCIH));

		return vo;
	}
	
	//formata campos de data para as notificacoes
	public List<NotificacoesGeraisVO> formatarCampos(List<NotificacoesGeraisVO> lista) {
		List<NotificacoesGeraisVO> resultado = new ArrayList<NotificacoesGeraisVO>();
		
		for (NotificacoesGeraisVO notificacao : lista) {
			if (notificacao.getDtFimCirurgia() != null) {
				notificacao.setDtFimCirurgiaFormatado(DateUtil.obterDataFormatada(notificacao.getDtFimCirurgia(), FORMATO_DATA));
			} else {
				notificacao.setDtFimCirurgiaFormatado("");
			}
			
			if (notificacao.getDtInicio() != null) {
				notificacao.setDtInicioFormatado(DateUtil.obterDataFormatada(notificacao.getDtInicio(), FORMATO_DATA));
			} else {
				notificacao.setDtInicioFormatado("");
			}
			
			if (notificacao.getDtFim() != null) {
				notificacao.setDtFimFormatado(DateUtil.obterDataFormatada(notificacao.getDtFim(), FORMATO_DATA));
			} else {
				notificacao.setDtFimFormatado("");
			}
			
			if (notificacao.getEtgDescricao() == null) {
				notificacao.setEtgDescricao("");
			} else {
				notificacao.setEtgDescricao(StringUtil.trim(notificacao.getEtgDescricao()));
			}
			
			resultado.add(notificacao);
		}
		return (resultado != null && resultado.isEmpty()) ? null : resultado;
	}
	
	public List<ItemMedicamentoPrescricaoMedicaDetalheVO> montarMedicamentosVO(List<MpmItemPrescricaoMdto> lista) {
		List<ItemMedicamentoPrescricaoMedicaDetalheVO> retorno = new ArrayList<ItemMedicamentoPrescricaoMedicaDetalheVO>();
		
		for (MpmItemPrescricaoMdto item : lista) {
			ItemMedicamentoPrescricaoMedicaDetalheVO vo = new ItemMedicamentoPrescricaoMedicaDetalheVO();
			
			if (item.getMedicamento() != null) {
				vo.setDescricao(item.getMedicamento().getDescricao());
			}
			
			if (item.getUsoMdtoAntimicrobia() != null) {
				vo.setIndUsoAntiMicrobiano(item.getUsoMdtoAntimicrobia().toString());
			}
			
			if (item.getPrescricaoMedicamento() != null) {
				if (item.getPrescricaoMedicamento().getDthrFim() != null) {
					vo.setDthrFim(DateUtil.obterDataFormatada(item.getPrescricaoMedicamento().getDthrFim(), FORMATO_DATA));
				} else {
					vo.setDthrFim("");
				}
				
				vo.setDthrInicio(DateUtil.obterDataFormatada(item.getPrescricaoMedicamento().getDthrInicio(), FORMATO_DATA));
				
				if (item.getPrescricaoMedicamento().getDuracaoTratSolicitado() != null) {
					vo.setDuracaoTratSolicitado(item.getPrescricaoMedicamento().getDuracaoTratSolicitado().toString());
				} else {
					vo.setDuracaoTratSolicitado("");
				}
			}
			
			if (item.getJustificativaUsoMedicamento() != null) {
				if (item.getJustificativaUsoMedicamento().getOrientacaoAvaliador() != null) {
					vo.setIndOrientacaoAvaliador((item.getJustificativaUsoMedicamento().getOrientacaoAvaliador()) ? "S" : "N");
				} 
				
				if (item.getJustificativaUsoMedicamento().getTipoInfeccao() != null) {
					vo.setTipoInfeccao(item.getJustificativaUsoMedicamento().getTipoInfeccao().getDescricao());
				} 
				
				if (item.getJustificativaUsoMedicamento().getNomeGerme() != null) {
					vo.setNomeGerme(item.getJustificativaUsoMedicamento().getNomeGerme());
				} 
			}
			
			retorno.add(vo);
		}
		
		return retorno;
	}
}
