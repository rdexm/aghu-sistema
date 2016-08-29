package br.gov.mec.aghu.internacao.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.Date;
import java.util.Iterator;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.lang3.StringUtils;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.business.IAghuFacade;
import br.gov.mec.aghu.constante.ConstanteAghCaractUnidFuncionais;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.business.seguranca.Secure;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.dominio.DominioCaracEspecialidade;
import br.gov.mec.aghu.dominio.DominioGrupoConvenio;
import br.gov.mec.aghu.dominio.DominioSexo;
import br.gov.mec.aghu.internacao.dao.AinMovimentoInternacaoDAO;
import br.gov.mec.aghu.internacao.dao.AinQuartosDAO;
import br.gov.mec.aghu.internacao.pesquisa.business.IPesquisaInternacaoFacade;
import br.gov.mec.aghu.internacao.vo.AltasPorUnidadeVO;
import br.gov.mec.aghu.model.AghClinicas;
import br.gov.mec.aghu.model.AghUnidadesFuncionais;
import br.gov.mec.aghu.model.AinLeitos;
import br.gov.mec.aghu.model.AinQuartos;
import br.gov.mec.aghu.model.AinTiposAltaMedica;

/**
 * @author Stanley Araujo *
 * **/
@SuppressWarnings("PMD.CyclomaticComplexity")
@Stateless
public class AltasPorUnidadeON extends BaseBusiness {

	@EJB
	private RelatorioInternacaoRN relatorioInternacaoRN;

	@EJB
	private AltasPorUnidadeRN altasPorUnidadeRN;

	private static final Log LOG = LogFactory.getLog(AltasPorUnidadeON.class);

	@Override
	@Deprecated
	protected Log getLogger() {

		return LOG;

	}

	@Inject
	private AinQuartosDAO ainQuartosDAO;

	@EJB
	private IAghuFacade aghuFacade;

	@Inject
	private AinMovimentoInternacaoDAO ainMovimentoInternacaoDAO;

	@EJB
	private IPesquisaInternacaoFacade pesquisaInternacaoFacade;

	private static final long serialVersionUID = -3791160547010985058L;

	/**
	 * 
	 * @author Stanley Araujo
	 * 
	 *         Realiza a pesquisa com os parâmetros informados
	 * 
	 * @param Data
	 *            de referência
	 * @param Grupo
	 *            convênio
	 * @param Código
	 *            da unidade funcional
	 * @return Lista de VO
	 * */
	@Secure("#{s:hasPermission('alta','pesquisar')}")
	@SuppressWarnings({"PMD.ExcessiveMethodLength", "PMD.NPathComplexity"})
	public List<AltasPorUnidadeVO> pesquisa(Date dataDeReferencia,
			DominioGrupoConvenio grupoConvenio, Integer codigoUnidadesFuncionais) throws ApplicationBusinessException {

		List<Object[]> res = getAinMovimentoInternacaoDAO().pesquisaAltasPorUnidade(dataDeReferencia, grupoConvenio, codigoUnidadesFuncionais);
		List<AltasPorUnidadeVO> lista = new ArrayList<AltasPorUnidadeVO>();
		SimpleDateFormat data = new SimpleDateFormat("dd/MM/yyyy");
		SimpleDateFormat dataAno = new SimpleDateFormat("dd/MM/yy");
		SimpleDateFormat hora = new SimpleDateFormat("HH:mm");

		// Criando lista de VO.
		Iterator<Object[]> it = res.iterator();
		while (it.hasNext()) {

			Object[] obj = it.next();
			AltasPorUnidadeVO vo = new AltasPorUnidadeVO();

			Integer tmiSeq = null;
			Short unfSeq = null;
			Short espSeq = null;
			Integer intSeq = null;
			Date dataHoraLancamento = null;
			AghUnidadesFuncionais unidadeFuncionais = null;
			String convenio = ((DominioGrupoConvenio) obj[0]).getDescricao();

			if (obj[1] != null) {
				tmiSeq = (Integer) obj[1];
			}
			if (obj[18] != null) {
				unfSeq = (Short) obj[18];
			}
			if (obj[9] != null) {
				intSeq = (Integer) obj[9];
			}
			if (obj[3] != null) {
				dataHoraLancamento = (Date) obj[3];
			}
			if (obj[19] != null) {
				espSeq = (Short) obj[19];
			}

			if (!restricao4(tmiSeq, intSeq, dataHoraLancamento, unfSeq, espSeq)) {
				continue;
			}

			if (codigoUnidadesFuncionais != null && !restricao5(tmiSeq, unfSeq, intSeq, dataHoraLancamento,
					codigoUnidadesFuncionais)) {
				continue;
			}

			if (tmiSeq != null && tmiSeq == 21) {
				unidadeFuncionais = obterUnidadeFuncional(unfSeq);
				vo.setGrupoConvenio(StringUtils.rightPad(convenio, 50, "")
						+ " Data: " + data.format((Date) obj[2]));
				vo.setDthrAltaMedica(hora.format((Date) obj[2]));
			} else {
				unidadeFuncionais = obterUnidadeFuncional(getAltasPorUnidadeRN()
						.obterUnfSeqOrigem(intSeq, dataHoraLancamento));
				vo.setGrupoConvenio(StringUtils.rightPad(convenio, 50, "")
						+ " Data: " + data.format(dataHoraLancamento));
				vo.setDthrAltaMedica(hora.format(dataHoraLancamento));
			}

			if (unidadeFuncionais != null) {
				vo.setUnidade(unidadeFuncionais.getLPADAndarAlaDescricao());
			}

			if (obj[4] != null) {
				String prontAux = ((Integer) obj[4]).toString();
				vo.setProntuario(prontAux.substring(0, prontAux.length() - 1)
						+ "/" + prontAux.charAt(prontAux.length() - 1));
			}

			if (obj[5] != null) {
				vo.setNome((String) obj[5]);
			}

			if (obj[6] != null) {
				DominioSexo sexo = ((DominioSexo) obj[6]);
				vo.setSexo(sexo.getDescricao().substring(0, 1));
			}

			String ltoLtoID = null;
			if (obj[7] != null) {
				AinLeitos leito = (AinLeitos) obj[7];
				ltoLtoID = leito.getLeitoID();
			}

			Short qrtNumero = null;
			if (obj[8] != null) {
				AinQuartos quarto = (AinQuartos) obj[8];
				qrtNumero = quarto.getNumero();
			}
			ltoLtoID = gerarLTOLTOID(tmiSeq, ltoLtoID, qrtNumero,
					unidadeFuncionais.getAndar(), intSeq, dataHoraLancamento);
			vo.setLtoLtoId(ltoLtoID);

			if (obj[10] != null) {
				AghClinicas clinica = ((AghClinicas) obj[10]);
				vo.setClcCodigo(String.valueOf(clinica.getCodigo()));
			}

			if (obj[11] != null) {
				vo.setSigla((String) obj[11]);
			}

			if (obj[12] != null) {
				vo.setCarater((String) obj[12]);
			}

			if (obj[13] != null && obj[14] != null) {
				vo.setNroRegConselho(getPesquisaInternacaoFacade()
						.buscarNroRegistroConselho((Short) obj[13],
								(Integer) obj[14]));
				vo.setProf(getPesquisaInternacaoFacade().buscarNomeUsual(
						(Short) obj[13], (Integer) obj[14]));
			}

			if (obj[15] != null) {
				vo.setDthrInternacao(dataAno.format((Date) obj[15]));
			}

			if (obj[16] != null) {
				AinTiposAltaMedica tiposAltaMedica = (AinTiposAltaMedica) obj[16];
				if (tmiSeq != null && tmiSeq == 21) {
					if (tiposAltaMedica.getCodigo().equals("C")
							|| tiposAltaMedica.getCodigo().equals("D")) {
						vo.setIndDifClasse("O");
					} else {
						vo.setIndDifClasse("A");
					}
				} else {
					vo.setIndDifClasse("T");
				}
			}

			if (obj[17] != null) {
				vo.setCodSus(((Integer) obj[17]).toString());
			}
			vo.setSenha(getPesquisaInternacaoFacade().buscaSenhaInternacao(intSeq));

			lista.add(vo);
		}

		// Ordenação por unidade.
		Collections.sort(lista, new AltasPorUnidadeComparator());

		return lista;
	}

	

	/**
	 * Impelemtação do seguinte comando no ORACLE:
	 * decode(mvi.tmi_seq,21,nvl(INT1
	 * .LTO_LTO_ID,nvl(to_char(int1.qrt_numero),lpad
	 * (to_char(unf1.andar),2,'0')))
	 * ,aink_ih_util.local_origem(mvi.int_seq,mvi.dthr_lancamento)) LTO_LTO_ID
	 * 
	 * @author Stanley Araujo
	 * @param Sequência
	 *            de tipos movimentos internação
	 * @param Número
	 *            do leito
	 * @param Número
	 *            do quarto
	 * @param Número
	 *            sequencial da internação
	 * @param Data
	 *            do Lançamento
	 * @return
	 * */

	private String gerarLTOLTOID(Integer tmiSeq, String ltoLtoID, Short qrtNumero, String andar, Integer seqInternacao, Date dataHoraLancamento) {

		if (tmiSeq == 21) {// TODO: Altertar 21 para o domini Correspondente

			if (ltoLtoID != null) {
				return ltoLtoID;
			} else {
				if (qrtNumero != null) {
					AinQuartos quarto = this.getQuartosDAO().obterPorChavePrimaria(qrtNumero);				
					return quarto.getDescricao();
				} else {
					return andar;
				}

			}
		} else {
			return getAltasPorUnidadeRN().obterLocalOrigem(seqInternacao,
					dataHoraLancamento);
		}
	}

	private AinQuartosDAO getQuartosDAO() {
		return ainQuartosDAO;
		
	}



	/**
	 * Impelemtação do seguinte comando no ORACLE:
	 * decode(mvi.tmi_Seq,21,mvi.unf_Seq
	 * ,aink_ih_util.unf_Seq_origem(mvi.int_seq,mvi.dthr_lancamento))
	 * 
	 * @author Stanley Araujo
	 * @param Sequência
	 *            de tipos movimentos internação
	 * @param Número
	 *            sequencial da unidade funcional
	 * @param Número
	 *            sequencial da internação
	 * @param Data
	 *            do Lançamento
	 * @return
	 * */
	private Integer restricao6(Integer tmiSeq, Short seqUnidadeFuncional,
			Integer seqInternacao, Date dataHoraLancamento) {

		if (tmiSeq == 21) { // TODO: Altertar 21 para o domini Correspondente
			return seqUnidadeFuncional.intValue();
		} else {
			return (getAltasPorUnidadeRN().obterUnfSeqOrigem(seqInternacao,
					dataHoraLancamento)).intValue();
		}

	}

	/**
	 * Impelemtação do seguinte comando no ORACLE:
	 * decode(mvi.tmi_Seq,21,mvi.unf_Seq
	 * ,aink_ih_util.unf_Seq_origem(mvi.int_seq,mvi.dthr_lancamento)) =
	 * nvl(:p_unidade,decode(mvi.tmi_Seq,21,mvi.unf_Seq,
	 * aink_ih_util.unf_Seq_origem(mvi.int_seq,mvi.dthr_lancamento)) )
	 * 
	 * @author Stanley Araujo
	 * @param Sequência
	 *            de tipos movimentos internação
	 * @param Número
	 *            sequencial da unidade funcional
	 * @param Número
	 *            sequencial da internação
	 * @param Data
	 *            do Lançamento
	 * @param Código
	 *            da unidade
	 * @return
	 * */

	private boolean restricao5(Integer tmiSeq, Short seqUnidadeFuncional,
			Integer seqInternacao, Date dataHoraLancamento, Integer unidade) {
		
		if (tmiSeq == 21) {
			return (seqUnidadeFuncional == unidade.shortValue());
		} else {
			Integer valor = restricao6(tmiSeq, seqUnidadeFuncional, seqInternacao,
					dataHoraLancamento);
			return (valor == unidade);
		}
	}

	/**
	 * Impelemtação do seguinte comando:and(mvi.tmi_Seq + 0 = 21 or (
	 * (mvi.tmi_Seq + 0 in (14,15) and mvi.unf_seq!=
	 * aink_ih_util.unf_Seq_origem(mvi.int_seq,mvi.dthr_lancamento) ) and
	 * restrincao3 ) )
	 * 
	 * @author Stanley Araujo
	 * @param Sequência
	 *            de tipos movimentos internação
	 * @param Número
	 *            sequencial da internação
	 * @param Data
	 *            do Lançamento
	 * @param Número
	 *            sequencial da unidade funcional
	 * @param Código
	 *            da unidade
	 * @return
	 * */
	private boolean restricao4(Integer tmiSeq, Integer seqInternacao,
			Date dataHoraLancamento, Short seqUnidadeFuncional,
			Short seqEspecialidade) {
		boolean primeiraCondicao = false;
		boolean segundaCondicao = false;
		boolean terceiraCondicao = false;
		boolean quartaCondicao = false;

		primeiraCondicao = (tmiSeq + 0 == 21);// TODO: Altertar 21 para o domini
		// Correspondente
		segundaCondicao = (14 == (tmiSeq + 0) || 15 == (tmiSeq + 0));// TODO:
		// Altertar
		// 14,15
		// para
		// o
		// domini
		// Correspondente
		terceiraCondicao = seqUnidadeFuncional != getAltasPorUnidadeRN()
				.obterUnfSeqOrigem(seqInternacao, dataHoraLancamento);
		quartaCondicao = restricao3(seqInternacao, dataHoraLancamento,
				seqUnidadeFuncional, seqEspecialidade);

		return (primeiraCondicao || (segundaCondicao && terceiraCondicao && quartaCondicao));
	}

	/**
	 * Impelemtação do seguinte comando: (restrincao2) or
	 * aghc_ver_caract_unf(aink_ih_util.unf_Seq_origem(mvi.int_seq
	 * ,mvi.dthr_lancamento),'CO') = 'S' OR (restrincao1)
	 * 
	 * @author Stanley Araujo
	 * @param Número
	 *            sequencial da internação
	 * @param Data
	 *            do Lançamento
	 * @param Número
	 *            sequencial da unidade funcional
	 * @param Código
	 *            da especialidade
	 * @return
	 * */
	private boolean restricao3(Integer seqInternacao, Date dataHoraLancamento,
			Short seqUnidadeFuncional, Short seqEspecialidade) {
		boolean primeiraCondicao = false;
		boolean segundaCondicao = false;
		boolean terceiraCondicao = false;
		primeiraCondicao = restricao2(seqInternacao, dataHoraLancamento,
				seqUnidadeFuncional);

		segundaCondicao = getRelatorioInternacaoRN()
				.verificarCaracteristicaUnidadeFuncional(getAltasPorUnidadeRN()
						.obterUnfSeqOrigem(seqInternacao, dataHoraLancamento),
						ConstanteAghCaractUnidFuncionais.CO);

		terceiraCondicao = restricao1(seqInternacao, dataHoraLancamento,
				seqEspecialidade);

		return primeiraCondicao || segundaCondicao || terceiraCondicao;
	}

	/**
	 * Impelemtação do seguinte comando:
	 * aghc_ver_caract_unf(aink_ih_util.unf_Seq_origem
	 * (mvi.int_seq,mvi.dthr_lancamento),'Laudo CTI') = 'S' and
	 * aghc_ver_caract_unf(mvi.unf_seq,'Laudo CTI') != 'S'
	 * 
	 * @author Stanley Araujo
	 * @param Número
	 *            sequencial da internação
	 * @param Data
	 *            do Lançamento
	 * @param Número
	 *            sequencial da unidade funcional
	 * @return
	 * */

	private boolean restricao2(Integer seqInternacao, Date dataHoraLancamento,
			Short seqUnidadeFuncional) {
		boolean primeiraCondicao = false;
		boolean segundaCondicao = false;

		primeiraCondicao = getRelatorioInternacaoRN()
				.verificarCaracteristicaUnidadeFuncional(getAltasPorUnidadeRN()
						.obterUnfSeqOrigem(seqInternacao, dataHoraLancamento),
						ConstanteAghCaractUnidFuncionais.LAUDO_CTI);

		segundaCondicao = !getRelatorioInternacaoRN()
				.verificarCaracteristicaUnidadeFuncional(seqUnidadeFuncional,
						ConstanteAghCaractUnidFuncionais.LAUDO_CTI);

		return (primeiraCondicao && segundaCondicao);
	}

	/**
	 * Impelemtação do seguinte
	 * comando:aghc_ver_caract_esp(aink_ih_util.esp_Seq_origem
	 * (mvi.int_seq,mvi.dthr_lancamento),'Central Leitos') = 'S' and mvi.esp_seq
	 * != aink_ih_util.esp_Seq_origem(mvi.int_seq,mvi.dthr_lancamento)
	 * 
	 * @author Stanley Araujo
	 * @param Número
	 *            sequencial da internação
	 * @param Data
	 *            do Lançamento
	 * @param Número
	 *            sequencial da especialidade
	 * @return
	 * */

	private boolean restricao1(Integer seqInternacao, Date dataHoraLancamento,
			Short seqEspecialidade) {

		boolean primeiraCondicao = false;
		boolean segundaCondicao = false;

		primeiraCondicao = getAltasPorUnidadeRN()
				.verificarCaracteristicaEspecialidade(getAltasPorUnidadeRN()
						.obterUnfSeqOrigem(seqInternacao, dataHoraLancamento),
						DominioCaracEspecialidade.CENTRAL_LEITOS);

		segundaCondicao = seqEspecialidade != getAltasPorUnidadeRN()
				.obterUnfSeqOrigem(seqInternacao, dataHoraLancamento);

		return (primeiraCondicao && segundaCondicao);
	}

	/**
	 * Retorna unidade funcional
	 * 
	 * @return
	 */
	private AghUnidadesFuncionais obterUnidadeFuncional(Short unfSeq) {
		return getAghuFacade().obterUnidadeFuncional(unfSeq);
	}

	/**
	 * Classe comparadora utilizada para ordenar a lista de
	 * <code>AltasPorUnidadeVO</code>
	 * 
	 * @author lalegre
	 * 
	 */
	class AltasPorUnidadeComparator implements Comparator<AltasPorUnidadeVO> {

		@Override
		public int compare(AltasPorUnidadeVO o1, AltasPorUnidadeVO o2) {
			
			String convenio1 = ((AltasPorUnidadeVO) o1).getGrupoConvenio();
			String convenio2 = ((AltasPorUnidadeVO) o2).getGrupoConvenio();

			String unidade1 = ((AltasPorUnidadeVO) o1).getUnidade();
			String unidade2 = ((AltasPorUnidadeVO) o2).getUnidade();
			
			if (convenio1.compareTo(convenio2) == 0) {
				return unidade1.compareTo(unidade2);
			} 
			
			return convenio1.compareTo(convenio2);
		}
	}

	protected AltasPorUnidadeRN getAltasPorUnidadeRN(){
		return altasPorUnidadeRN;
	}

	protected RelatorioInternacaoRN getRelatorioInternacaoRN(){
		return relatorioInternacaoRN;
	}

	protected IPesquisaInternacaoFacade getPesquisaInternacaoFacade() {
		return pesquisaInternacaoFacade;
	}

	protected AinMovimentoInternacaoDAO getAinMovimentoInternacaoDAO() {
		return ainMovimentoInternacaoDAO;
	}

	protected IAghuFacade getAghuFacade() {
		return this.aghuFacade;
	}

}
