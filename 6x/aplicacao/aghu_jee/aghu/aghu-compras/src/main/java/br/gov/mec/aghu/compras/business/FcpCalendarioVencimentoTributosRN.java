/**
 * 
 */
package br.gov.mec.aghu.compras.business;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.List;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import br.gov.mec.aghu.compras.dao.FcpCalendarioVencimentoTributosDAO;
import br.gov.mec.aghu.compras.dao.FcpCalendarioVencimentoTributosJnDAO;
import br.gov.mec.aghu.compras.vo.FcpCalendarioVencimentoTributosVO;
import br.gov.mec.aghu.configuracao.dao.AghFeriadosDAO;
import br.gov.mec.aghu.dominio.DominioDiasMes;
import br.gov.mec.aghu.dominio.DominioMesVencimento;
import br.gov.mec.aghu.dominio.DominioTipoTributo;
import br.gov.mec.aghu.dominio.DominioVencimentoDiaNaoUtil;
import br.gov.mec.aghu.model.FcpCalendarioVencimentoTributos;
import br.gov.mec.aghu.model.FcpCalendarioVencimentoTributosJn;
import br.gov.mec.aghu.model.RapServidores;
import br.gov.mec.aghu.registrocolaborador.business.IServidorLogadoFacade;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.utils.DateUtil;

/**
 * @author julianosena
 *
 */

@Stateless
public class FcpCalendarioVencimentoTributosRN extends BaseBusiness {

	private static final long serialVersionUID = -5216359093986655202L;
	private static final Log LOG = LogFactory.getLog(FcpCalendarioVencimentoTributosRN.class);

	@Inject
	private FcpCalendarioVencimentoTributosDAO fcpCalendarioVencimentoTributosDAO;

	@Inject
	private FcpCalendarioVencimentoTributosJnDAO fcpCalendarioVencimentoTributosJnDAO;
	
	@Inject
	private AghFeriadosDAO aghFeriadosDAO;

	@EJB
	private IServidorLogadoFacade iServidorLogadoFacade;

	/**
	 * Códigos para lançamento de exceptions
	 * 
	 * @author julianosena
	 *
	 */
	public enum FcpCalendarioVencimentoRNException implements BusinessExceptionCode {
		MENSAGEM_PERIODO_APURACAO_NAO_PODE_SOBREPOR_OUTRO,
		MENSAGEM_PERIODO_APURACAO_DEVE_SER_INFERIOR_AO_FIM
	}

	/**
	 * Insere um registro na tabela da entidade
	 * Lança exceção se os períodos da apuração se sobrepõem
	 * 
	 * @param inicioApuracao
	 * @param fimApuracao
	 * @param tipoTributo
	 * @return
	 * @throws ApplicationBusinessException 
	 */
	public FcpCalendarioVencimentoTributos persistirCalendarioVencimento( FcpCalendarioVencimentoTributos fcpCalendarioVencimento )
			throws ApplicationBusinessException {

		//Recupero os valores da entidade recebida para persistência
		DominioDiasMes inicioPeriodoApuracao = fcpCalendarioVencimento.getInicioPeriodo();
		DominioDiasMes fimPeriodoApuracao = fcpCalendarioVencimento.getFimPeriodo();
		Date inicioVigencia = fcpCalendarioVencimento.getInicioVigencia();
		DominioTipoTributo tipoTributo = fcpCalendarioVencimento.getTipoTributo();

		//Verifica se os periodos se sobrepoem
		boolean sobrepoe = this.getFcpCalendarioVencimentoTributosDAO()
								.verificarSobreposicaoPeriodos(inicioPeriodoApuracao, fimPeriodoApuracao, inicioVigencia, tipoTributo);

		//RN01
		//Se o período sobrepõe um intervalo do outro
		//lance uma exception para avisar o usuário
		if( sobrepoe ){
			this.getLogger().error("Os períodos da apuração não podem ser sobrepostos");
			throw new ApplicationBusinessException(FcpCalendarioVencimentoRNException.MENSAGEM_PERIODO_APURACAO_NAO_PODE_SOBREPOR_OUTRO);

		//RN02
		//Se o período de apuração inicial é maior que o final
		} else if ( inicioPeriodoApuracao.getCodigo() > fimPeriodoApuracao.getCodigo() ) {
			this.getLogger().error("O período inicial é maior que o periodo de apuração final");
			throw new ApplicationBusinessException(FcpCalendarioVencimentoRNException.MENSAGEM_PERIODO_APURACAO_DEVE_SER_INFERIOR_AO_FIM);
			
		} else {
			//Chamo a camada DAO para persistência do objeto
			this.getLogger().debug("Objeto persistido em: " + new Date());
			FcpCalendarioVencimentoTributos fcpCalendarioVencimentoTributosOriginal = this.getFcpCalendarioVencimentoTributosDAO().merge(fcpCalendarioVencimento);
			
			//Configuro a entidade journal com os dados da entidade que será persistida
			FcpCalendarioVencimentoTributosJn fcpCalendarioVencimentoTributosJn = new FcpCalendarioVencimentoTributosJn();
			fcpCalendarioVencimentoTributosJn.setDiaVencimento(fcpCalendarioVencimentoTributosOriginal.getDiaVencimento());
			fcpCalendarioVencimentoTributosJn.setFatoGerador(fcpCalendarioVencimentoTributosOriginal.getFatoGerador());
			fcpCalendarioVencimentoTributosJn.setFimPeriodo(fcpCalendarioVencimentoTributosOriginal.getFimPeriodo());
			fcpCalendarioVencimentoTributosJn.setInicioPeriodo(fcpCalendarioVencimentoTributosOriginal.getInicioPeriodo());
			fcpCalendarioVencimentoTributosJn.setInicioVigencia(fcpCalendarioVencimentoTributosOriginal.getInicioVigencia());
			fcpCalendarioVencimentoTributosJn.setMesVencimento(fcpCalendarioVencimentoTributosOriginal.getMesVencimento());
			fcpCalendarioVencimentoTributosJn.setNomeUsuario(this.getIServidorLogadoFacade().obterServidorLogado().getUsuario());
			fcpCalendarioVencimentoTributosJn.setObservacao(fcpCalendarioVencimentoTributosOriginal.getObservacao());
			fcpCalendarioVencimentoTributosJn.setOperacao(DominioOperacoesJournal.INS);
			fcpCalendarioVencimentoTributosJn.setTipoTributo(fcpCalendarioVencimentoTributosOriginal.getTipoTributo());
			fcpCalendarioVencimentoTributosJn.setVencimentoDiaNaoUtil(fcpCalendarioVencimentoTributosOriginal.getVencimentoDiaNaoUtil());
			fcpCalendarioVencimentoTributosJn.setSeq(fcpCalendarioVencimentoTributosOriginal.getSeq());
			
			this.getFcpCalendarioVencimentoTributosJnDAO().merge(fcpCalendarioVencimentoTributosJn);
			this.getLogger().debug("Objeto journal persistido em: " + new Date());

			return fcpCalendarioVencimentoTributosOriginal;
		}
	}
	
	/**
	 * Consulta por chave primária 
	 * @param numeroCalendarioVencimento
	 * @return
	 */
	public FcpCalendarioVencimentoTributos pesquisarFcpCalendarioVencimentoTributoPorCodigo(Integer numeroCalendarioVencimento){
		return this.getFcpCalendarioVencimentoTributosDAO().obterPorChavePrimaria(numeroCalendarioVencimento);
	}

	/**
	 * Retorna registro da tabela da entidade de acordo com o vencimento por apuração
	 * 
	 * @param inicioApuracao
	 * @param fimApuracao
	 * @param tipoTributo
	 * @return
	 */
	private List<FcpCalendarioVencimentoTributos> pesquisar( Date inicioApuracao, Date fimApuracao,
			DominioTipoTributo tipoTributo ) {

		return this.getFcpCalendarioVencimentoTributosDAO()
					.pesquisarCalendarioVencimentoPorApuracao( inicioApuracao, fimApuracao, tipoTributo);
	}

	/**
	 * Retorna os VOs para listagem de tela de pesquisa
	 * de calendario vencimento tributos
	 * 
	 * @param dataApuracao
	 * @param tipoTributo
	 * @return
	 */
	public List<FcpCalendarioVencimentoTributosVO> pesquisarFcpCalendarioVencimentoTributo(Date dataApuracao, DominioTipoTributo tipoTributo) {
        Calendar calendarioDataApuracao = Calendar.getInstance();
        calendarioDataApuracao.setTime(dataApuracao);
        
        Calendar calendarioInicioApuracao = Calendar.getInstance();
        calendarioInicioApuracao.setTime(dataApuracao);
        calendarioInicioApuracao.set(Calendar.DAY_OF_MONTH, 1);
        
        Date inicioApuracao = calendarioInicioApuracao.getTime();
        
        Calendar calendarioFimApuracao = Calendar.getInstance();
        calendarioFimApuracao.setTime(dataApuracao);
        calendarioFimApuracao.set(Calendar.DAY_OF_MONTH, calendarioDataApuracao.getActualMaximum(Calendar.DAY_OF_MONTH));
        Date fimApuracao = calendarioFimApuracao.getTime();

        //Lista de calendarios
		List<FcpCalendarioVencimentoTributos> calendarioVencimentoTributosList = this.pesquisar(inicioApuracao, fimApuracao, tipoTributo);

		//Montando VOs
		List<FcpCalendarioVencimentoTributosVO> calendarioVencimentoTributosVOList = new ArrayList<FcpCalendarioVencimentoTributosVO>();

		//Construo a lista de VOs de acordo com as entidades retornadas do banco com a consulta C2
		for( FcpCalendarioVencimentoTributos fcpCalendarioVencimentoTributos : calendarioVencimentoTributosList ){
			FcpCalendarioVencimentoTributosVO vo = this.getVO(calendarioDataApuracao, fcpCalendarioVencimentoTributos);

			calendarioVencimentoTributosVOList.add(vo);
		}

		return calendarioVencimentoTributosVOList;
	}

	/**
	 * Recupera o VO da listagem da tela de pesquisa
	 * de calendario vencimento tributos RN de acordo com a entidade passada como
	 * parâmetro
	 * 
	 * @param calendarApuracao
	 * @param fcpCalendarioVencimentoTributos
	 * @return
	 */
	private FcpCalendarioVencimentoTributosVO getVO(Calendar calendarApuracao, FcpCalendarioVencimentoTributos fcpCalendarioVencimentoTributos){
		DominioTipoTributo tributo = fcpCalendarioVencimentoTributos.getTipoTributo();
		Integer mesApuracao = calendarApuracao.get(Calendar.MONTH);

		//Construindo formatação do período de apuração
		String diaInicioPeriodoApuracao = String.format("%02d", fcpCalendarioVencimentoTributos.getInicioPeriodo().getCodigo());
		String diaFimPeriodoApuracao = String.format("%02d", fcpCalendarioVencimentoTributos.getFimPeriodo().getCodigo());
		String mesPeriodoApuracao = String.format("%02d", mesApuracao + 1);
		String anoPeriodoApuracao = String.valueOf(calendarApuracao.get(Calendar.YEAR));
		String periodoApuracao = diaInicioPeriodoApuracao + "/" + mesPeriodoApuracao + "/" + anoPeriodoApuracao + " à " + diaFimPeriodoApuracao + "/" + mesPeriodoApuracao + "/" + anoPeriodoApuracao;

		//Construindo formatação para a data de vencimento
		//de acordo com a regra RN03
		Calendar calendar = Calendar.getInstance();
		calendar.set(Calendar.DAY_OF_MONTH, fcpCalendarioVencimentoTributos.getDiaVencimento().getCodigo());
		calendar.set(Calendar.YEAR, calendarApuracao.get(Calendar.YEAR));
		calendar.set(Calendar.MONTH, mesApuracao);
		//Adiciono mês se o usuário selecionou opção para adicionar na interface
		if(!fcpCalendarioVencimentoTributos.getMesVencimento().equals(DominioMesVencimento.MES_APURACAO) ){
			calendar.add(Calendar.MONTH, fcpCalendarioVencimentoTributos.getMesVencimento().getCodigo());
		}
		Date dataVencimento = calendar.getTime();
		
		// Trtamento caso exceda o ano 9999
		if (calendar.get(Calendar.YEAR) >= 10000) {
			Calendar calendarAux = Calendar.getInstance();
	        calendarAux.setTime(calendarApuracao.getTime());
	        calendarAux.set(Calendar.MONTH, calendarAux.getActualMaximum(Calendar.MONTH));
	        calendarAux.set(Calendar.DAY_OF_MONTH, fcpCalendarioVencimentoTributos.getDiaVencimento().getCodigo());
	        dataVencimento = calendarAux.getTime();
		}

		//Recupero a ação a ser realizada com a data do vencimento
		//Postergar, Atrasar ou Manter
		DominioVencimentoDiaNaoUtil vencimentoDiaNaoUtil = fcpCalendarioVencimentoTributos.getVencimentoDiaNaoUtil();

		//Recupero a data do vencimento já validada
		dataVencimento = this.getDataVencimento(vencimentoDiaNaoUtil, dataVencimento);

		//Formato a data de vencimento e data de início vigência
		SimpleDateFormat simpleDateFormat = new SimpleDateFormat("dd/MM/yyyy");
		String dataVencimentoFormatada = simpleDateFormat.format(dataVencimento);
		String dataInicioVigencia = simpleDateFormat.format(fcpCalendarioVencimentoTributos.getInicioVigencia());

		//Construindo VO
			// 1 - Setando os valores originais
			FcpCalendarioVencimentoTributosVO vo = new FcpCalendarioVencimentoTributosVO();
			vo.setSeq(fcpCalendarioVencimentoTributos.getSeq());
			vo.setInicioVigencia(fcpCalendarioVencimentoTributos.getInicioVigencia());
			vo.setTipoTributo(fcpCalendarioVencimentoTributos.getTipoTributo());
			vo.setFatoGerador(fcpCalendarioVencimentoTributos.getFatoGerador());
			vo.setVencimentoDiaNaoUtil(fcpCalendarioVencimentoTributos.getVencimentoDiaNaoUtil());
			vo.setInicioPeriodo(fcpCalendarioVencimentoTributos.getInicioPeriodo());
			vo.setFimPeriodo(fcpCalendarioVencimentoTributos.getFimPeriodo());
			vo.setMesVencimento(fcpCalendarioVencimentoTributos.getMesVencimento());
			vo.setObservacao(fcpCalendarioVencimentoTributos.getObservacao());

			// 2 - Setando os valores formatados para tela
			vo.getFormattedFields().setTipoTributo(tributo.getDescricao());
			vo.getFormattedFields().setPeriodoApuracao(periodoApuracao);
			vo.getFormattedFields().setVencimento(dataVencimentoFormatada);
			vo.getFormattedFields().setInicioVigencia(dataInicioVigencia);
		

		return vo;
	}

	/**
	 * Basedado em uma data de vencimento
	 * modifica-a de acordo com feriados e dias úteis
	 * 
	 * @param acao
	 * @param dataVencimento
	 * @return
	 */
	public Date getDataVencimento( DominioVencimentoDiaNaoUtil acao, Date dataVencimento ){

		if(!acao.equals(DominioVencimentoDiaNaoUtil.M) ){

			while( DateUtil.isFinalSemana(dataVencimento) || getAghFeriadosDAO().verificarExisteFeriado(dataVencimento) ){
				switch( acao ){
					case P:
						dataVencimento = DateUtil.adicionaDias(dataVencimento, 1);
						break;
					case A:
						dataVencimento = DateUtil.adicionaDias(dataVencimento, -1);
						break;
					default:
						break;
				}
			}
		}

		return dataVencimento;
	}
	/**
	 * Método para remover um registro de um calendário de vencimento do tributo.
	 * @param calendarioVencimentoTributo Calendário do vencimento do tributo a ser removido
	 * */
	public void remover(FcpCalendarioVencimentoTributos calendarioVencimentoTributos) {
		calendarioVencimentoTributos = this.getFcpCalendarioVencimentoTributosDAO().obterPorChavePrimaria(calendarioVencimentoTributos.getSeq());
		
		RapServidores servidorLogado = iServidorLogadoFacade.obterServidorLogado();
		final FcpCalendarioVencimentoTributosJn journal = BaseJournalFactory.getBaseJournal(DominioOperacoesJournal.DEL, FcpCalendarioVencimentoTributosJn.class, servidorLogado.getUsuario());
		journal.setSeq(calendarioVencimentoTributos.getSeq().intValue());
		journal.setDiaVencimento(calendarioVencimentoTributos.getDiaVencimento());
		journal.setFatoGerador(calendarioVencimentoTributos.getFatoGerador());
		journal.setFimPeriodo(calendarioVencimentoTributos.getFimPeriodo());
		journal.setInicioPeriodo(calendarioVencimentoTributos.getInicioPeriodo());
		journal.setInicioVigencia(calendarioVencimentoTributos.getInicioVigencia());
		journal.setMesVencimento(calendarioVencimentoTributos.getMesVencimento());
		journal.setObservacao(calendarioVencimentoTributos.getObservacao());
		journal.setTipoTributo(calendarioVencimentoTributos.getTipoTributo());
		journal.setVencimentoDiaNaoUtil(calendarioVencimentoTributos.getVencimentoDiaNaoUtil());
		
		getFcpCalendarioVencimentoTributosJnDAO().persistir(journal);		
		
		this.getFcpCalendarioVencimentoTributosDAO().remover(calendarioVencimentoTributos);
	}

	@Override
	protected Log getLogger() {
		return FcpCalendarioVencimentoTributosRN.LOG;
	}

	public FcpCalendarioVencimentoTributosDAO getFcpCalendarioVencimentoTributosDAO() {
		return fcpCalendarioVencimentoTributosDAO;
	}
	
	public FcpCalendarioVencimentoTributosJnDAO getFcpCalendarioVencimentoTributosJnDAO() {
		return fcpCalendarioVencimentoTributosJnDAO;
	}
	
	public IServidorLogadoFacade getIServidorLogadoFacade() {
		return iServidorLogadoFacade;
	}

	public AghFeriadosDAO getAghFeriadosDAO() {
		return aghFeriadosDAO;
	}
	
}
