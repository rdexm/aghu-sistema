package br.gov.mec.aghu.paciente.business;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import javax.ejb.Stateless;
import javax.inject.Inject;
import javax.persistence.PersistenceException;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.dominio.DominioSimNao;
import br.gov.mec.aghu.dominio.DominioSituacao;
import br.gov.mec.aghu.dominio.DominioSituacaoMovimentoProntuario;
import br.gov.mec.aghu.dominio.DominioTodosUltimo;
import br.gov.mec.aghu.model.AghAla;
import br.gov.mec.aghu.model.AghOrigemEventos;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AipFinalidadesMovimentacao;
import br.gov.mec.aghu.model.AipMovimentacaoProntuarios;
import br.gov.mec.aghu.paciente.dao.AipMovimentacaoProntuarioDAO;
import br.gov.mec.aghu.paciente.dao.AipSolicitantesProntuarioDAO;
import br.gov.mec.aghu.paciente.vo.RelatorioMovimentacaoVO;
import br.gov.mec.aghu.paciente.vo.VAipSolicitantesVO;

/**
 * Classe responsável por prover os métodos de negócio para geração do Relatório
 * de Movimentações.
 * 
 * @author Ricardo Costa
 * 
 */
@Stateless
public class MovimentacaoON extends BaseBusiness {

	private static final Log LOG = LogFactory.getLog(MovimentacaoON.class);
	
	@Override
	@Deprecated
	protected Log getLogger() {
		return LOG;
	}
	
	
	@Inject
	private AipMovimentacaoProntuarioDAO aipMovimentacaoProntuarioDAO;
	
	@Inject
	private AipSolicitantesProntuarioDAO aipSolicitantesProntuarioDAO;

	/**
	 * 
	 */
	private static final long serialVersionUID = 5560175216945159633L;

	private enum MovimentacaoONExceptionCode implements BusinessExceptionCode {
		CAMPO_NUMERICO, NAO_HA_DADOS_DATA, ERRO_AO_GERAR_ETIQUETAS, IMPRESSAO_CANCELADA, ERRO_REMOVER_MOVIMENTACAO_PRONTUARIO, ERRO_REMOCAO_MOVIMENTACAO_PRONTUARIO_CONCLUIDA, ERRO_REMOCAO_MOVIMENTACAO_PRONTUARIO_SITUACAO_RETIRADO, DATA_INICIAL_MAIOR_DATA_FINAL;;
		
		public void throwException() throws ApplicationBusinessException {
			throw new ApplicationBusinessException(this);
		}
	}
	
	/**
	 * ORADB View V_AIP_SOLICITANTES
	 * 
	 * @return List
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public List<VAipSolicitantesVO> obterViewAipSolicitantes(Short seq) {
		List<Object[]> res = this.getAipSolicitantesProntuarioDAO().listarViewSolicitantesProntuario(seq);

		// Criando lista de VO.
		List<VAipSolicitantesVO> lista = new ArrayList<VAipSolicitantesVO>(0);

		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();

			VAipSolicitantesVO vo = new VAipSolicitantesVO();

			if (obj[0] != null) {
				vo.setCodigo((Short) obj[0]);
			}

			String andarAux = StringUtils.EMPTY;

			if (obj[1] != null) {
				vo.setOrigemEventos((AghOrigemEventos) obj[1]);
				if (obj[6] != null) {
					vo.setDescricao((String) obj[6]);
				}
			} else {
				if (obj[2] != null) {
					vo.setDescricao((String) obj[2]);
				} else {
					if (obj[3] != null && StringUtils.isNotBlank(obj[3].toString())) {
						andarAux = obj[3].toString();
						if (andarAux.equals("0")) {
							if (obj[4] != null) {
								vo.setDescricao((String) obj[4]);
							}
						} else {
							vo.setDescricao(andarAux);
							if (obj[5] != null) {
								AghAla domAla = (AghAla) obj[5];
								vo.setDescricao(vo.getDescricao() + " "
										+ domAla.getDescricao());
							}
							if (obj[4] != null) {
								vo.setDescricao(vo.getDescricao() + " - "
										+ (String) obj[4]);
							}
						}
					}
				}
			}

			if (obj[7] != null) {
				DominioSimNao separacaoPrevia = (DominioSimNao) obj[7];
				vo.setSeparacaoPrevia(separacaoPrevia);
			}

			if (obj[8] != null) {
				DominioTodosUltimo volumesManuseados = (DominioTodosUltimo) obj[8];
				vo.setVolumesManuseados(volumesManuseados);
			}

			if (obj[9] != null) {
				DominioSituacao indSituacao = (DominioSituacao) obj[9];
				vo.setIndSituacao(indSituacao);
			}

			if (obj[10] != null) {
				AghUnidadesFuncionais unidadesFuncionais = (AghUnidadesFuncionais) obj[10];
				vo.setUnidadesFuncionais(unidadesFuncionais);
			}

			if (obj[11] != null) {
				AipFinalidadesMovimentacao finalidadesMovimentacao = (AipFinalidadesMovimentacao) obj[11];
				vo.setFinalidadesMovimentacao(finalidadesMovimentacao);
			}

			if (obj[12] != null) {
				DominioSimNao mensagemSamis = (DominioSimNao) obj[12];
				vo.setMensagemSamis(mensagemSamis);
			}

			lista.add(vo);
		}

		// Ordenação por descrição.
		Collections.sort(lista, new AreaSolicitanteComparator());

		return lista;
	}

	/**
	 * ORADB PROCEDURE RN_PSPP_EXCLUI_MVP PACKAGE AIPK_PSP
	 * 
	 */
	public void validaRemocaoMovimentacaoProntuario(
			Integer codigoSolicitacaoProntuario, Integer codigoPaciente)
			throws ApplicationBusinessException {

		AipMovimentacaoProntuarios movimentacaoProntuarios = this
				.obterMovimentacaoPorSolicitacaoPaciente(
						codigoSolicitacaoProntuario, codigoPaciente);
		if (movimentacaoProntuarios != null
				&& movimentacaoProntuarios.getSolicitacao() != null
				&& movimentacaoProntuarios.getSolicitacao().getDtConclusao() != null) {
			throw new ApplicationBusinessException(
					MovimentacaoONExceptionCode.ERRO_REMOCAO_MOVIMENTACAO_PRONTUARIO_CONCLUIDA);

		} else if (movimentacaoProntuarios != null
				&& movimentacaoProntuarios.getSituacao() != null
				&& movimentacaoProntuarios.getSituacao().equals(
						DominioSituacaoMovimentoProntuario.R)) {
			throw new ApplicationBusinessException(
					MovimentacaoONExceptionCode.ERRO_REMOCAO_MOVIMENTACAO_PRONTUARIO_SITUACAO_RETIRADO);

		} else if (movimentacaoProntuarios != null) {
			this.excluir(movimentacaoProntuarios);
		}
	}
	
	/**
	 * Obter Movimentacao Por Solicitacao Paciente
	 * 
	 * @param codigoSolicitacaoProntuario
	 * @param codigoPaciente
	 * @return
	 */
	private AipMovimentacaoProntuarios obterMovimentacaoPorSolicitacaoPaciente(
			Integer codigoSolicitacaoProntuario, Integer codigoPaciente) {
		return this.getAipSolicitantesProntuarioDAO().obterMovimentacaoPorSolicitacaoPaciente(codigoSolicitacaoProntuario, codigoPaciente);
	}

	private void excluir(AipMovimentacaoProntuarios movimentacaoProntuario)
			throws ApplicationBusinessException {
		try {
			this.getAipMovimentacaoProntuarioDAO().remover(movimentacaoProntuario);
			this.getAipMovimentacaoProntuarioDAO().flush();
		} catch (PersistenceException e) {
			logError("Exceção capturada: ", e);
			throw new ApplicationBusinessException(
					MovimentacaoONExceptionCode.ERRO_REMOVER_MOVIMENTACAO_PRONTUARIO);
		}
	}

	/**
	 * Método usado para obter as 'Movimentações por Situação' no relatório de
	 * mesmo nome.
	 * 
	 * @param dtInicial
	 * @param dtFinal
	 * @param csSituacao
	 * @param csExibirArea
	 * @param vAipSolicitantes
	 * @return List de <code>RelatorioMovimentacaoVO</code>
	 */
	@SuppressWarnings("PMD.NPathComplexity")
	public List<RelatorioMovimentacaoVO> obterMovimentacoes(Date dtInicial,
			Date dtFinal, DominioSituacaoMovimentoProntuario csSituacao,
			Boolean csExibirArea, VAipSolicitantesVO vAipSolicitantes, Map<Short, VAipSolicitantesVO> hashSeqVAipSolicitantes) {
		List<Object[]> res = this.getAipSolicitantesProntuarioDAO().obterMovimentacoes(dtInicial, dtFinal, csSituacao, csExibirArea, vAipSolicitantes);

		// Criando lista de VO.
		List<RelatorioMovimentacaoVO> lista = new ArrayList<RelatorioMovimentacaoVO>(
				0);

		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {
			Object[] obj = it.next();

			RelatorioMovimentacaoVO vo = new RelatorioMovimentacaoVO();

			if (obj[0] != null) {
				if (csExibirArea != null && !csExibirArea) {
					vo.setCodigo("0");
				} else {
					vo.setCodigo(((Short) obj[0]).toString());
					VAipSolicitantesVO auxVAipVo = null;
					auxVAipVo = hashSeqVAipSolicitantes
							.get((Short) obj[0]);
					if (auxVAipVo != null) {
						vo.setDescricao(auxVAipVo.getDescricao());
					}
				}
			}

			if (obj[2] != null) {
				// Tranforma número de 124567 para 12456/7
				String prontAux = ((Integer) obj[2]).toString();
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)
						+ "/" + prontAux.charAt(prontAux.length() - 1));
			}

			if (obj[3] != null) {
				vo.setNome(obj[3].toString());
			}

			if (obj[4] != null) {
				vo.setLtoLtoId(obj[4].toString());
			}

			if (obj[5] != null) {
				vo.setCodigo2(((Integer) obj[5]).toString());
			}

			SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");

			if (obj[6] != null) {
				// TODO: Passar Date e tratar no JASPER.
				Date dtAux = (Date) obj[6];
				vo.setDataMovimento(sdf.format(dtAux));
			}

			if (csSituacao != null
					&& csSituacao.getDescricao().equals("Devolvido")) {
				if (obj[8] != null) {
					Date dtAux = (Date) obj[8]; // mvp.dataDevolucao
					vo.setDataRtDv(sdf.format(dtAux));
				}
			} else {
				if (obj[7] != null) {
					Date dtAux = (Date) obj[7]; // mvp.dataRetirada
					vo.setDataRtDv(sdf.format(dtAux));
				}
			}

			if (obj[9] != null) {
				Date dtAux = (Date) obj[9]; // mvp.dataLocalizacao
				vo.setDataLocal(sdf.format(dtAux));
			}

			if (obj[10] != null) {
				vo.setVolumes(((Short) obj[10]).toString());
			} else {
				vo.setVolumes("0");
			}

			lista.add(vo);
		}

		// Ordenação por seção do prontuário.
		// substr(to_char(PAC.PRONTUARIO,'09999999'),7,2)
		// Ex: Para o prontuário 123401/5 o que deve ser considerado para
		// ordenação é o número 01.
		Collections.sort(lista, new ProntuarioComparator());

		return lista;
	}

	/**
	 * Método utilizado pelo Sugegstion Box para pesquisar por id ou descrição.
	 * 
	 * @param filtro
	 * @return Lista de <code>VAipSolicitantesVO</code>
	 */
	public List<VAipSolicitantesVO> pesquisarAreaSolicitante(
			List<VAipSolicitantesVO> lista, String filtro) {

		List<VAipSolicitantesVO> res = new ArrayList<VAipSolicitantesVO>(0);

		Iterator<VAipSolicitantesVO> it = lista.iterator();
		while (it.hasNext()) {
			VAipSolicitantesVO item = it.next();
			if (CoreUtil.isNumeroShort(filtro)) {
				if (item.getCodigo() == Short.parseShort(filtro)) {
					res.add(item);
				}
			} else {
				if (item.getDescricao().toUpperCase().contains(
						filtro.toUpperCase())) {
					res.add(item);
				}
			}
		}
		return res;
	}
	
	protected AipSolicitantesProntuarioDAO getAipSolicitantesProntuarioDAO() {
		return aipSolicitantesProntuarioDAO;
	}

	protected AipMovimentacaoProntuarioDAO getAipMovimentacaoProntuarioDAO() {
		return aipMovimentacaoProntuarioDAO;
	}

}

/**
 * Classe comparadora utilizada para ordenar a lista de
 * <code>RelatorioMovimentacaoVO</code> pela seção do prontuário, que é definida
 * por dois dígitos no numero do prontuário. Ex: Um prontuário como número
 * 123401/5 possui o número do seção 01, representado pelo 5 e 6 dígito. E os
 * itens tem vir ordenados por esse campo.
 * 
 * @author Ricardo Costa
 * 
 */
class ProntuarioComparator implements Comparator<RelatorioMovimentacaoVO> {

	@Override
	@SuppressWarnings("PMD.AvoidThrowingRawExceptionTypes")
	public int compare(RelatorioMovimentacaoVO o1, RelatorioMovimentacaoVO o2) {

		int area1 = Integer
				.parseInt(((RelatorioMovimentacaoVO) o1).getCodigo());
		int area2 = Integer
				.parseInt(((RelatorioMovimentacaoVO) o2).getCodigo());

		if (area1 > area2) {
			return 1;
		} else if (area1 < area2) {
			return -1;
		} else {
			// Exemplo: o1 = 388208/3 e o2 = 233208/8
			String vo1Desc = ((RelatorioMovimentacaoVO) o1).getProntuario();
			String vo2Desc = ((RelatorioMovimentacaoVO) o2).getProntuario();

			// Exemplo: secao1 = 08 e secao2 = 08
			int secao1 = Integer.parseInt(vo1Desc.substring(
					vo1Desc.length() - 4, vo1Desc.length() - 2));

			int secao2 = Integer.parseInt(vo2Desc.substring(
					vo2Desc.length() - 4, vo2Desc.length() - 2));

			if (secao1 > secao2) {
				return 1;
			} else if (secao1 < secao2) {
				return -1;
			} else {
				int pre1 = 0;
				int pre2 = 0;
				// Exemplo: secao1 = 3882 e secao2 = 2332
				if (vo1Desc.length() > 4) {
					pre1 = Integer.parseInt(vo1Desc.substring(0, vo1Desc
							.length() - 4));
				}
				if (vo2Desc.length() > 4) {
					pre2 = Integer.parseInt(vo2Desc.substring(0, vo2Desc
							.length() - 4));
				}
				if (pre1 > pre2) {
					return 1;
				} else if (pre1 < pre2) {
					return -1;
				} else {
					SimpleDateFormat sdf = new SimpleDateFormat("dd/MM/yyyy");
					Date date1 = null;
					Date date2 = null;
					try {
						date1 = sdf.parse(o1.getDataMovimento());
						date2 = sdf.parse(o2.getDataMovimento());
					} catch (ParseException e) {
						// Subir exceção
						throw new RuntimeException(e);
					}
					if (date1.after(date2)) {
						return 1;
					} else if (date1.before(date2)) {
						return -1;
					} else {
						return 0;
					}
				}
			}
		}
	}
}

/**
 * Classe comparadora utilizada para ordenar a lista de
 * <code>VAipSolicitantesVO</code> pela descrição.
 * 
 * @author Ricardo Costa
 * 
 */
class AreaSolicitanteComparator implements Comparator<VAipSolicitantesVO> {

	@Override
	public int compare(VAipSolicitantesVO o1, VAipSolicitantesVO o2) {

		String s1 = ((VAipSolicitantesVO) o1).getDescricao();
		String s2 = ((VAipSolicitantesVO) o2).getDescricao();

		return s1.compareTo(s2);
	}
}
