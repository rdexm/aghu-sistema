package br.gov.mec.aghu.perinatologia.business;

import java.util.Date;

import javax.ejb.EJB;
import javax.ejb.Stateless;
import javax.inject.Inject;

import org.apache.commons.logging.Log;

import br.gov.mec.aghu.ambulatorio.service.IAmbulatorioService;
import br.gov.mec.aghu.casca.business.ICascaFacade;
import br.gov.mec.aghu.dominio.DominioEventoLogImpressao;
import br.gov.mec.aghu.emergencia.producer.QualificadorUsuario;
import br.gov.mec.aghu.model.McoAnamneseEfs;
import br.gov.mec.aghu.model.McoConduta;
import br.gov.mec.aghu.model.McoLogImpressoes;
import br.gov.mec.aghu.model.McoPlanoIniciais;
import br.gov.mec.aghu.model.McoPlanoIniciaisId;
import br.gov.mec.aghu.model.McoPlanoIniciaisJn;
import br.gov.mec.aghu.model.RapServidoresId;
import br.gov.mec.aghu.perinatologia.dao.McoLogImpressoesDAO;
import br.gov.mec.aghu.perinatologia.dao.McoPlanoIniciaisDAO;
import br.gov.mec.aghu.perinatologia.dao.McoPlanoIniciaisJnDAO;
import br.gov.mec.aghu.registrocolaborador.dao.RapServidoresDAO;
import br.gov.mec.aghu.registrocolaborador.service.IRegistroColaboradorService;
import br.gov.mec.aghu.registrocolaborador.vo.Usuario;
import br.gov.mec.aghu.core.business.BaseBusiness;
import br.gov.mec.aghu.core.commons.CoreUtil;
import br.gov.mec.aghu.core.commons.seguranca.IPermissionService;
import br.gov.mec.aghu.core.dominio.DominioOperacoesJournal;
import br.gov.mec.aghu.core.exception.ApplicationBusinessException;
import br.gov.mec.aghu.core.exception.BusinessExceptionCode;
import br.gov.mec.aghu.core.factory.BaseJournalFactory;
import br.gov.mec.aghu.core.locator.ServiceLocator;
import br.gov.mec.aghu.core.utils.AGHUUtil;
import br.gov.mec.aghu.core.utils.DateUtil;
import br.gov.mec.aghu.service.ServiceException;

@Stateless
public class RegistrarConsultaCORN extends BaseBusiness {

	private static final long serialVersionUID = 4126313594932214952L;
	private static final int RANGE_INI_DIALATACAO = 0;
	private static final int RANGE_FIM_DIALATACAO = 10;
	private static final String IDENTIFICADOR_MODULO_AGEANDAMENTO_EXAME = "agendamentoExames";
	private static final String  CONSELHO_PROFISSIONAL = "CREMERS";
	
	@EJB
	ICascaFacade cascaFacade;
	
	@Inject
	IRegistroColaboradorService registroColaboradorService;
	
	@Inject
	IAmbulatorioService ambulatorioService;
	
	@Inject @QualificadorUsuario
	private Usuario usuario;
	
	@Inject
	private McoPlanoIniciaisDAO mcoPlanoIniciaisDAO;
	
	@Inject
	private McoPlanoIniciaisJnDAO mcoPlanoIniciaisJnDAO;
	
	@Inject
	private McoLogImpressoesDAO mcoLogImpressoesDAO;
	
	@Inject
	private RapServidoresDAO servidorDAO;
	
	private enum RegistrarConsultaCORNExceptionCode implements BusinessExceptionCode {
		MSG_REG_CONS_CO_MCO_00532,
		MSG_REG_CONS_CO_MCO_00546_1,
		ERRO_CONSULTA_JA_FINALIZADA,
		ERRO_EXECUTAR_SERVICO,
		ERRO_USUARIO_SEM_PERMISSAO_SOLICITACAO_EXAMES
	}

	@Override
	protected Log getLogger() {
		return null;
	}
	
	public void validarConsultaFinalizada(Integer pacCodigo, Short seqp, Integer numeroConsulta) throws ApplicationBusinessException {
		DominioEventoLogImpressao[] eventos = new DominioEventoLogImpressao[]{DominioEventoLogImpressao.MCOR_CONSULTA_OBS};
		if(mcoLogImpressoesDAO.verificarExisteImpressao(pacCodigo, seqp, numeroConsulta, eventos)){
			throw new ApplicationBusinessException(RegistrarConsultaCORNExceptionCode.ERRO_CONSULTA_JA_FINALIZADA);
		}
	}
	
	public void verificaPermissoesImpressaoRelatorio(Integer matricula, Short codigo, String usuarioLogado) throws ApplicationBusinessException {
		String[] sigla = {CONSELHO_PROFISSIONAL};
		try {
			boolean permissaoImpressao = registroColaboradorService.usuarioTemPermissaoParaImprimirRelatoriosDefinitivos(matricula, codigo, sigla);
			if(!(permissaoImpressao && (getPermissionService().usuarioTemPermissao(usuarioLogado, "elaborarSolicitacaoExame", "elaborar") || getPermissionService().usuarioTemPermissao(usuarioLogado, "solicitarExamesLoteAssinar", "elaborar")))) {
				throw new ApplicationBusinessException(RegistrarConsultaCORNExceptionCode.ERRO_USUARIO_SEM_PERMISSAO_SOLICITACAO_EXAMES);
			}
		 } catch (ServiceException e) {
			throw new ApplicationBusinessException(RegistrarConsultaCORNExceptionCode.ERRO_EXECUTAR_SERVICO, e.getMessage());
		}
	}
	
	public Integer obterAtendimentoPorNumConsulta(Integer numConsulta) {
		return ambulatorioService.obterAtendimentoPorConNumero(numConsulta);
	}
	
	public boolean moduloAgendamentoExameAtivo() {
		return cascaFacade.verificarSeModuloEstaAtivo(IDENTIFICADOR_MODULO_AGEANDAMENTO_EXAME);
	}

	public void excluirCondutas(McoPlanoIniciais planoIniciais, McoAnamneseEfs anamneseEfs) {
		
		this.inserirJournalPlanoInciais(planoIniciais, usuario.getLogin(), DominioOperacoesJournal.DEL);
		
		planoIniciais = this.mcoPlanoIniciaisDAO.obterPorChavePrimaria(planoIniciais.getId());
		this.mcoPlanoIniciaisDAO.remover(planoIniciais);
	}
	
	private void inserirJournalPlanoInciais(McoPlanoIniciais planoIniciaisOriginal, String usuario, DominioOperacoesJournal operacao){
	
		McoPlanoIniciaisJn jn = BaseJournalFactory.getBaseJournal(operacao, McoPlanoIniciaisJn.class, usuario);
		
		jn.setConNumero(planoIniciaisOriginal.getMcoAnamneseEfs().getId().getConNumero());
		jn.setSeqp(planoIniciaisOriginal.getMcoAnamneseEfs().getId().getGsoSeqp());
		jn.setPacCodigo(planoIniciaisOriginal.getMcoAnamneseEfs().getId().getGsoPacCodigo());
		jn.setCondutaSeq(planoIniciaisOriginal.getConduta().getSeq());
		jn.setComplemento(planoIniciaisOriginal.getComplemento());
		jn.setCriadoEm(planoIniciaisOriginal.getCriadoEm());
		jn.setSerMatricula(planoIniciaisOriginal.getSerMatricula());
		jn.setSerVinCodigo(planoIniciaisOriginal.getSerVinCodigo());
		
		this.mcoPlanoIniciaisJnDAO.persistir(jn);
	}

	public void validarDataConsulta(Date dthrConsulta) throws ApplicationBusinessException {
		if(DateUtil.validaDataMaior(dthrConsulta, new Date()) || DateUtil.diffInDaysInteger(new Date(), dthrConsulta) > 3){
			throw new ApplicationBusinessException(RegistrarConsultaCORNExceptionCode.MSG_REG_CONS_CO_MCO_00532);
		}
	}

	public void verificarDilatacao(String dilatacao) throws ApplicationBusinessException {
		
		if(dilatacao != null && !dilatacao.isEmpty()){
			if (!CoreUtil.isBetweenRange(Integer.parseInt(dilatacao), RANGE_INI_DIALATACAO, RANGE_FIM_DIALATACAO)) {
				throw new ApplicationBusinessException(RegistrarConsultaCORNExceptionCode.MSG_REG_CONS_CO_MCO_00546_1);
			}
		}
	}
	
	// #25123 - RN06
	public void insereConduta(McoConduta conduta, McoAnamneseEfs anamneseEfs, String complemento) {
		
		McoPlanoIniciaisId id = new McoPlanoIniciaisId();
		id.setEfiConNumero(anamneseEfs.getId().getConNumero());
		id.setEfiGsoSeqp(anamneseEfs.getId().getGsoSeqp());
		id.setEfiGsoPacCodigo(anamneseEfs.getId().getGsoPacCodigo());
		id.setCdtSeq(conduta.getSeq());
		
		McoPlanoIniciais plano = new McoPlanoIniciais();
		plano.setId(id);
		plano.setComplemento(complemento);
		plano.setCriadoEm(new Date());
		plano.setSerMatricula(usuario.getMatricula());
		plano.setSerVinCodigo(usuario.getVinculo());
		
		this.mcoPlanoIniciaisDAO.persistir(plano);
	}

	// #25123 - RN06
	public void atualizaConduta(McoConduta conduta, McoAnamneseEfs anamneseEfs, String complemento) {
		
		McoPlanoIniciaisId id = new McoPlanoIniciaisId();
		id.setEfiConNumero(anamneseEfs.getId().getConNumero());
		id.setEfiGsoSeqp(anamneseEfs.getId().getGsoSeqp());
		id.setEfiGsoPacCodigo(anamneseEfs.getId().getGsoPacCodigo());
		id.setCdtSeq(conduta.getSeq());
		
		McoPlanoIniciais mcoPlanoIniciaisOriginal = this.mcoPlanoIniciaisDAO.obterOriginal(id);
		
		McoPlanoIniciais plano = this.mcoPlanoIniciaisDAO.obterPorChavePrimaria(id);
		
		plano.setComplemento(complemento);
		plano.setSerMatricula(usuario.getMatricula());
		plano.setSerVinCodigo(usuario.getVinculo());
		
		this.mcoPlanoIniciaisDAO.atualizar(plano);
		
		if(this.veificaAlteracaoConduduta(plano, mcoPlanoIniciaisOriginal)) {
			this.inserirJournalPlanoInciais(mcoPlanoIniciaisOriginal, usuario.getLogin(), DominioOperacoesJournal.UPD);
		}
	}

	private boolean veificaAlteracaoConduduta(McoPlanoIniciais mcoPlanoIniciais, McoPlanoIniciais mcoPlanoIniciaisOriginal) {
		return AGHUUtil.modificados(mcoPlanoIniciais.getComplemento(), mcoPlanoIniciaisOriginal.getComplemento()) || 
				AGHUUtil.modificados(mcoPlanoIniciais.getSerMatricula(), mcoPlanoIniciaisOriginal.getSerMatricula()) || 
				AGHUUtil.modificados(mcoPlanoIniciais.getSerVinCodigo(), mcoPlanoIniciaisOriginal.getSerVinCodigo());
	}

	/**
	 * RN07 de 26349
	 * 
	 * @ORADB MCOT_LOG_BRI
	 * 
	 *        Pré-Insert log de impressão
	 * 
	 * @param mcoLogImpressoes
	 */
	public void preInserir(McoLogImpressoes mcoLogImpressoes) {
		// Setar no campo MCO_LOG_IMPRESSOES.CRIADO_EM a data atual;
		mcoLogImpressoes.setCriadoEm(new Date());
		// Setar nos campos MCO_LOG_IMPRESSOES.SER_MATRICULA e MCO_LOG_IMPRESSOES.SER_VIN_CODIGO a matrícula e código do vínculo do usuário
		// logado no sistema.
	
		
		mcoLogImpressoes.setServidor(servidorDAO.obter(new RapServidoresId(usuario.getMatricula(),usuario.getVinculo())));
	}
	
	private IPermissionService getPermissionService(){
		return ServiceLocator.getBeanRemote(IPermissionService.class, "aghu-casca");
	}
	
	
}
