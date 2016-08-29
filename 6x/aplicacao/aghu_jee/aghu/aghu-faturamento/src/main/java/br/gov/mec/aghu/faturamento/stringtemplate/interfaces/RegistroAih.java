package br.gov.mec.aghu.faturamento.stringtemplate.interfaces;

import org.antlr.stringtemplate.StringTemplate;
import org.antlr.stringtemplate.StringTemplateGroup;

import br.gov.mec.aghu.faturamento.stringtemplate.renderer.AttributeFormatPair;

public interface RegistroAih {

	public interface NomeAtributoEnum {

		public abstract String getNome();
	}

	/**
	 * Verificar definicoes no arquivo: <code>arquivo_sus.sti</code>
	 * @author gandriotti
	 *
	 */
	public enum NomeStringTemplateEnum {

		COMUM("aih_comum"),
		ESPECIFICACAO("aih_especificacao"),
		IDENT_PACIENTE("aih_identificacao_paciente"),
		END_PACIENTE("aih_endereco_paciente"),
		INTERNACAO("aih_internacao"),
		PROC_SEC_ESP("aih_procedimentos_secundarios_especiais"),
		UTI_NEONATAL("aih_uti_neonatal"),
		ACIDENTE_TRABALHO("aih_acidente_de_trabalho"),
		PARTO("aih_parto"),
		DOC_PACIENTE("aih_documento_paciente"),
		REG_CIVIL("aih_registro_civil"),
		DADOS_OPM("aih_dados_opm"),
		NENHUM(""), ;

		private String nome;

		private NomeStringTemplateEnum(final String nome) {

			this.nome = nome;
		}

		public String getNome() {

			return this.nome;
		}
	}

	public abstract NomeStringTemplateEnum getNomeStringTemplate();

	public abstract NomeAtributoEnum[] getAtributos();

	public abstract void ajustarAtributos(StringTemplate st);

	public abstract String setFormat(NomeAtributoEnum atributo, String format);

	public abstract String getFormat(NomeAtributoEnum atributo);

	public abstract AttributeFormatPair getPair(NomeAtributoEnum atributo);

	public abstract StringTemplate obterStringTemplateComAtributos(StringTemplateGroup stg);

	public abstract String getDescricao();
}
